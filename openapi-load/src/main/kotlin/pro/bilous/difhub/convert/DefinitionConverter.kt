package pro.bilous.difhub.convert

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.swagger.v3.core.util.PrimitiveType
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import pro.bilous.difhub.config.DatasetStatus
import pro.bilous.difhub.load.IModelLoader
import pro.bilous.difhub.model.FieldsItem
import pro.bilous.difhub.model.Model

class DefinitionConverter(private val modelLoader: IModelLoader, private val source: Model, private val datasetStatus: DatasetStatus) {
	private val definitions = mutableMapOf<String, Schema<*>>()

	fun convert(): Map<String, Schema<*>> {
		val schema = createModelImpl(source)
		definitions[schema.name] = schema
		return definitions
	}

	private fun createModelImpl(model: Model): Schema<*> {
		val schemaName = normalizeTypeName(model.identity.name)
		val schema = when (model.`object`?.usage) {
			"Enum" -> createEnumSchema(model)
			else -> createObjectSchema(model)
		}
		schema.name = schemaName
		schema.description = model.identity.description

		if (model.`object` != null) {
			schema.addExtension("x-data-type", model.`object`.usage)
			val version = model.version!!
			schema.addExtension("x-version", "${version.major}.${version.minor}.${version.revision}")
			schema.addExtension("x-path", "${model.`object`.parent!!.name}/datasets/${model.identity.name}")
		}
		return schema
	}

	private fun createObjectSchema(model: Model): ObjectSchema {
		val schema = ObjectSchema()
		val identityName = model.identity.name
		model.structure?.fields?.forEach {
			val name = getFieldName(it)
			if (!shouldIgnore(identityName, name)) {
				schema.addProperties(name, fieldToProperty(it))
			}
		}
		schema.required = model.structure?.fields?.filter { !it.optional }?.map { getFieldName(it) }
		return schema
	}

	private fun createEnumSchema(model: Model): StringSchema {
		val enumModel = EnumConverter(model).convert()

		val schema = StringSchema()
		val validEnums = enumModel.values.filter { it.value != null }
		schema.enum = validEnums.map { it.value }

		schema.addExtension("x-enum-metadata", validEnums)
		return schema
	}

	private fun getFieldName(fieldsItem: FieldsItem): String {
		return fieldsItem.identity.name.decapitalize()
	}

	private fun shouldIgnore(modelName: String, propertyName: String): Boolean {
		if (modelName.startsWith("_") || propertyName.startsWith("_")) {
			return true
		}
		return (modelName == "Identity" && propertyName == "translations")
				|| ((modelName == "Entity" || modelName == "Error") && propertyName == "properties")
	}

	private fun fieldToProperty(item: FieldsItem): Schema<Any> {
		val property = when (item.type) {
			"Structure" -> createStructureProperty(item)
			"Reference" -> createReferenceProperty(item)
			"Enum" -> createEnumProperty(item)
			else -> createProperty(item)
		}

		if (item.count != null && item.count == 0) {
			return ArraySchema().apply {
				description = property.description
				items = property
			}
		}
		return property
	}

	private fun createEnumProperty(item: FieldsItem): Schema<Any> {
		return createStructureProperty(item)
	}

	private fun createReferenceProperty(item: FieldsItem): Schema<Any> {
		if (hasIdentityFormat(item)) {
			return createReferenceIdentity(item)
		}
		return createProperty(item)
	}

	private fun hasIdentityFormat(item: FieldsItem): Boolean {
		return item.format.startsWith(prefix = "identity", ignoreCase = true)
				|| readRefDataset(item.reference) == "Identity"
	}

	private fun createReferenceIdentity(item: FieldsItem): ComposedSchema {
		val property = ComposedSchema()
		property.description = item.identity.description

		val refDataset = "Reference${readRefDataset(item.reference)}"
		property.allOf = listOf(ObjectSchema().apply { `$ref` = refDataset })
		if (!definitions.containsKey(refDataset)) {
			definitions[refDataset] = createIdentitySchema(refDataset)
		}
		addExtensions(property, item)
		return property
	}

	private fun createIdentitySchema(refDataset: String): Schema<*> {
		val schema = ObjectSchema()

		val stringProperty = PrimitiveType.fromName("string").createProperty()
		schema.description = "Complex structure to describe referenced resource"

		schema.addProperties("id", stringProperty.apply {
			description = "Guid of the relationship structure $refDataset"
		})
		schema.addProperties("resourceId", stringProperty.apply {
			description = "Guid of the target Resource"
		})
		schema.addProperties("name", stringProperty.apply {
			description = "Name of the Reference, can be target Resource name or custom one"
		})

		val descriptionProperty = PrimitiveType.fromName("string").createProperty().apply {
			description = "Description of the Reference, can be target Resource description or custom one"
			addExtension("x-usage", "Description")
		}
		schema.addProperties("description", descriptionProperty)

		schema.addProperties("type", stringProperty.apply {
			description = "Name of the target Resource, required if resourceId designed to hold vary Resources types"
		})
		schema.addProperties("uri", stringProperty.apply {
			description = "Optional URI of the target Resource"
		})

		return schema
	}

	private val modelLoadingInProgress = mutableSetOf<String>()
	private fun createStructureProperty(item: FieldsItem): ComposedSchema {
		val property = ComposedSchema()

		property.description = item.identity.description
		val refDataset = readRefDataset(item.reference)
		property.allOf = listOf(ObjectSchema().apply { `$ref` = refDataset })

		property.description = item.identity.description
		if (!definitions.containsKey(refDataset) && !modelLoadingInProgress.contains(refDataset)) {
			val source = modelLoader.loadModel(item.reference, datasetStatus)
			modelLoadingInProgress.add(refDataset)
			if (source != null) {
				val schema = createModelImpl(source)
				definitions[refDataset] = schema
			}
			modelLoadingInProgress.remove(refDataset)
		}
		addExtensions(property, item)
		return property
	}

	private fun createProperty(item: FieldsItem): Schema<Any> {
		val type = normalizeType(item.type)

		val description = item.identity.description
		val format = if (type.format == "reference") readRefFormat(item) else type.format

		val primitiveType = PrimitiveType.fromName(type.type)
			?: error("Can not create primitive for type: ${type.type} , format: $format")

		val schema = primitiveType.createProperty()
		schema.format = format
		if (item.size > 0) {
			schema.maxLength = item.size
		}
		if (!description.isNullOrEmpty()) {
			schema.description = description
		}
		if (item.type == "Enum") {
			var source: Model? = null
			try {
				source = modelLoader.loadModel(item.reference, datasetStatus)
			} catch (e: MismatchedInputException) {
				println("Failed to load Enum ${item.reference}")
				println(e)
			}
			if (source != null) {
				val enumModel = EnumConverter(source).convert()
				schema.enum = enumModel.values.mapNotNull { it.value }
			}
		}
		addExtensions(schema, item)
		return schema
	}

	private fun addExtensions(schema: Schema<*>, item: FieldsItem) {
		schema.addExtension("x-data-type", item.type)
		schema.addExtension("x-usage", item.usage)
		val format = item.format.trim()
		if (format.isNotEmpty()) {
			schema.addExtension("x-format", format)
		}
		item.properties?.forEach {
			if (it.identity != null) {
				schema.addExtension("x-${it.identity.name}", it.value)
			}
		}
	}

	private fun readRefFormat(item: FieldsItem): String {
		val refParts = item.reference.split("/")
		val dataType = readReference("datasets", refParts)
		val application = readReference("applications", refParts)
		val system = readReference("systems", refParts)
		return "system: $system | application: $application | dataType: $dataType"
	}

	private fun readRefDataset(reference: String): String {
		val array = reference.split("/")
		return normalizeTypeName(array[array.lastIndexOf("datasets") + 1])
	}

	private fun readReference(name: String, parts: List<String>): String {
		return parts[parts.lastIndexOf(name) + 1]
	}

	private fun normalizeType(type: String) = TypesConverter.convert(type)

}
