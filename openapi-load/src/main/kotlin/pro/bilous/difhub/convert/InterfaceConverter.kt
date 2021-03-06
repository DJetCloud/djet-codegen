package pro.bilous.difhub.convert

import io.swagger.v3.core.util.PrimitiveType
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.HeaderParameter
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.PathParameter
import io.swagger.v3.oas.models.parameters.QueryParameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import pro.bilous.difhub.model.Field
import pro.bilous.difhub.model.Model
import pro.bilous.difhub.model.OperationsItem
import pro.bilous.difhub.model.ParametersItem
import pro.bilous.difhub.model.ResponsesItem

class InterfaceConverter(private val source: Model) {

	val pathModels = mutableMapOf<String, String>()
	val parameters = mutableMapOf<String, Parameter>()
	val requestBodies = mutableMapOf<String, RequestBody>()
	val paths = mutableMapOf<String, PathItem>()

	private val pathRegisteredParams = mutableListOf<String>()
	private val sourcePath = source.path.replace("\u200B", "").trim()

	fun convert() {
		val path = PathItem()

		source.parameters?.forEach {
			addRequestBodyOrParameter(it)
		}
		source.operations?.forEach {
			addOperation(it, path)
		}
		paths[sourcePath] = path
		postProcessOperations()
	}

	private fun addRequestBodyOrParameter(item: ParametersItem) {
		val identityName = item.field!!.identity.name
		val location = if (sourcePath.contains("{${identityName}}")) {
			"Path"
		} else {
			item.location
		}

		if (location == "Body") {
			requestBodies[identityName] = createRequestBody(item)
		} else {
			val param = when(location) {
				"Header" -> createHeaderParameter(item)
				"Path" -> createPathParameter(item)
				"Query" -> createQueryParameter(item)
				else -> throw IllegalStateException("Location '$location' of interface parameter is invalid")
			}
			val supportedParam = !identityName.startsWith("_")
			if (supportedParam) {
				val prefix = when (location) {
					"Header" -> source.identity.name + "-"
					else -> ""
				}
				parameters[prefix + param.name] = param
			}
		}
	}

	private fun postProcessOperations() {
		if (!sourcePath.endsWith("}")) {
			return
		}
		val primaryPathParamName = sourcePath.split("/").last().removePrefix("{").removeSuffix("}")
		val pathParam = source.parameters?.find { it.field!!.identity.name == primaryPathParamName }
		if (pathParam == null || !pathParam.field!!.optional) {
			return
		}
		val extraPathItem = PathItem()
		val pathItem = paths[sourcePath]!!

		val postOperation = pathItem.post
		pathItem.post = null

		val getOperation = pathItem.get
		val getResponses = wrap200ResponseAsArray(getOperation.responses)

		extraPathItem.get = Operation().apply {
			operationId = "${getOperation.operationId}List"
			responses = getResponses
			tags = getOperation.tags
			summary = "${getOperation.summary}List"
			description = getOperation.description
			parameters = getOperation.parameters
		}
		extraPathItem.post = postOperation
		if (pathItem.parameters != null) {
			extraPathItem.parameters = pathItem.parameters.filter {
				when {
					it.`$ref` != null -> it.`$ref`.split("/").last() != primaryPathParamName
					it.name != null -> it.name != primaryPathParamName
					else -> false
				}
			}
		}
		val extraPathLine = sourcePath.removeSuffix("/{$primaryPathParamName}")

		paths[extraPathLine] = extraPathItem
		if (pathItem.get.parameters.isNullOrEmpty()) {
			System.err.println("Illegal missing get parameters: ${pathItem.get.summary}")
		} else {
			pathItem.get.parameters = pathItem.get.parameters.filter { it.`$ref`.split("/").last() != "search" }
		}
	}

	private fun wrap200ResponseAsArray(apiResponses: ApiResponses): ApiResponses {
		if (!apiResponses.containsKey("200") && !apiResponses.containsKey("201") ) {
			return apiResponses
		}
		val get200Response = when {
			apiResponses.containsKey("200") -> apiResponses["200"]
			apiResponses.containsKey("201") -> apiResponses["201"]
			else -> throw IllegalArgumentException("Success response was not found. Fix your DifHub Interface!")
		}

		if (get200Response!!.content == null) {
			return apiResponses
		}
		val objectSchema = get200Response.content["application/json"]?.schema ?: return apiResponses

		return ApiResponses().apply {
			apiResponses.forEach {
				addApiResponse(it.key, it.value)
			}
			addApiResponse("200", ApiResponse().apply {
				description = get200Response.description
				content = Content().addMediaType("application/json", MediaType().schema(ArraySchema().items(objectSchema)))
			})
		}
	}

	private fun addOperation(item: OperationsItem, path: PathItem) {
		val op = Operation()

		if (!source.`object`?.tags.isNullOrEmpty()) {
			source.`object`?.tags?.forEach {
				op.addTagsItem(it.name)
			}
		} else {
			op.addTagsItem(source.identity.name)
		}

		val operationId = "${source.identity.name.decapitalize()}${item.identity.name.capitalize()}"

		op.operationId = operationId
		op.summary = item.identity.name
		op.description = item.identity.description

		item.parameters?.forEach {
			addParameter(it, op, path)
		}
		item.responses?.forEach {
			if (op.responses == null) {
				op.responses = ApiResponses()
			}
			if (findResponse(it.name) != null) {
				val (key, value) = createResponse(it)
				op.responses.addApiResponse(key, value)
			}
		}
		path.operation(PathItem.HttpMethod.valueOf(item.action.toUpperCase()), op)
	}

	private fun addParameter(paramSource: ParametersItem, op: Operation, path: PathItem) {
		val secondSourceParam = findParameter(paramSource.name) ?: return

		val fullParam = ParametersItem(
			id = paramSource.id,
			name = paramSource.name,
			description = paramSource.description,
			field = secondSourceParam.field,
			location = secondSourceParam.location
		)
		val param = createParameter(fullParam)

//		if (fullParam.location == "Header" && fullParam.name == "bearer") {
//			// Header Bearer parameter not supported
//			return
//		}
		if (fullParam.name.startsWith("_")) {
			// Underscore hides parameter from code generation
			return
		}
		if (fullParam.location == "Body") {
			op.requestBody(RequestBody().`$ref`(paramSource.name))
			return
		}

		when(parameters[paramSource.name]) {
			is PathParameter -> {
				if (!pathRegisteredParams.contains(fullParam.name)) {
					path.addParametersItem(param)
					pathRegisteredParams.add(fullParam.name)
				}
			}
			else -> op.addParametersItem(param)
		}
	}

	private fun findParameter(name: String): ParametersItem? {
		return source.parameters?.findLast { it.field!!.identity.name == name }
	}

	private fun findResponse(name: String): ResponsesItem? {
		return source.responses?.findLast { it.field != null && it.field.identity.name == name }
	}

	private fun createParameter(item: ParametersItem): Parameter {
		val itemName = "${if (item.location == "Header") source.identity.name + "-" else ""}${item.name}"
		return Parameter().apply { `$ref` = itemName }
	}

	private fun createHeaderParameter(item: ParametersItem): Parameter {
		val param = HeaderParameter()
		item.field!!
		param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.required = !item.field.optional
		if (item.field.access > 0) {
			param.extensions = mapOf(
				"x-auth-access" to item.field.access,
				"x-auth-format" to item.field.format
			)
		}
		param.schema = createSchema(item.field, useFormat = false)

		return param
	}

	private fun createPathParameter(item: ParametersItem): Parameter {
		val param = PathParameter()
		item.field!!
		param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.schema = createSchema(item.field)
		param.required = true //!item.field.optional should always be true.
		return param
	}

	private fun createSchema(field: Field, useFormat: Boolean = true): Schema<Any> {
		val format = if (useFormat) field.format.lowercase() else null
		val type = PrimitiveType.fromTypeAndFormat(field.type.lowercase(), format) ?: return ObjectSchema()

		return if (field.count == 0) {
			ArraySchema().apply {
				items = type.createProperty()
			}
		} else {
			type.createProperty()
		}
	}

	private fun createQueryParameter(item: ParametersItem): Parameter {
		val param = QueryParameter()
		item.field!!
		param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.schema = createSchema(item.field)
		param.required = !item.field.optional
		if (item.field.count == 0) {
			param.explode = false
		}
		return param
	}

	private fun createRequestBody(item: ParametersItem): RequestBody {
		val param = RequestBody()
		item.field!!
		//param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.required = !item.field.optional
		param.content(createContent(item.field))
		return param
	}

	private fun createResponse(it: ResponsesItem): Pair<String, ApiResponse> {
		val item = findResponse(it.name)!!

		val response = ApiResponse()
		response.description = item.field!!.identity.description

		if ("Structure" == item.field.type) {
			response.content(createContent(item.field))
			pathModels[getDefType(item.field.reference)] = item.field.reference
		}

		return Pair(item.code, response)
	}

	private fun createContent(field: Field): Content {
		val typeRef = "#/components/schemas/${getDefType(field.reference).capitalize()}"
		val typeSchema = Schema<Any>().`$ref`(typeRef)
		return Content().addMediaType("application/json",
			MediaType().schema(
					if (field.count == 0) {
						ArraySchema().items(typeSchema)
					} else {
						typeSchema
					}
			)
		)
	}

	private fun getDefType(reference: String): String {
		val array = reference.split("/")
		return array[array.lastIndexOf("datasets") + 1]
	}
}
