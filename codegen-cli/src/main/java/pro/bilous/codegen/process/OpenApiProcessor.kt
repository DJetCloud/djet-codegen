package pro.bilous.codegen.process

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.Parameter
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.utils.StringUtils.camelize
import org.openapitools.codegen.utils.URLPathUtils
import java.util.*

class OpenApiProcessor(val codegen: CodeCodegen) {

	private val additionalProperties: MutableMap<String, Any> = codegen.additionalProperties()

	fun preprocessOpenAPI(openAPI: OpenAPI) {
		setupServerPort(openAPI)

		if (!additionalProperties.containsKey(CodeCodegen.TITLE)) {
			// From the title, compute a reasonable name for the package and the API
			var title: String? = openAPI.info.title

			// Drop any API suffix
			if (title != null) {
				title = title.trim { it <= ' ' }.replace(" ", "-")
				if (title.toUpperCase(Locale.ROOT).endsWith("API")) {
					title = title.substring(0, title.length - 3)
				}

				codegen.setTitle(camelize(codegen.sanitizeName(title), true))
			}
			additionalProperties[CodeCodegen.TITLE] = codegen.getTitle()
		}

		if (!additionalProperties.containsKey(CodeCodegen.SERVER_PORT)) {
			val url = URLPathUtils.getServerURL(openAPI, null)
			this.additionalProperties[CodeCodegen.SERVER_PORT] = URLPathUtils.getPort(url, 8080)
		}

		if (openAPI.paths == null) {
			return
		}

		for (pathname in openAPI.paths.keys) {
			val path = openAPI.paths[pathname] ?: continue
			if (path.readOperations() == null) continue

			for (operation in path.readOperations()) {
				if (operation.tags == null) continue

				val tags = mutableListOf<MutableMap<String, String>>()
				for (tag in operation.tags) {
					val value = mutableMapOf<String, String>()
					value["tag"] = tag
					value["hasMore"] = "true"
					tags.add(value)
				}
				if (tags.size > 0) {
					tags[tags.size - 1].remove("hasMore")
				}
				if (operation.tags.size > 0) {
					val tag = operation.tags[0]
					operation.tags = mutableListOf(tag)
				}
				operation.addExtension("x-tags", tags)
			}
		}

		val guardsSet = mutableSetOf<Map<String, Any?>>()

		additionalProperties["authRules"] = createAuthRules(openAPI, guardsSet)
		additionalProperties["guardsSet"] = guardsSet
		additionalProperties["hasGuardsSet"] = guardsSet.isNotEmpty()
	}

	fun createAuthRules(openAPI: OpenAPI, guardsSet: MutableSet<Map<String, Any?>>): MutableSet<Map<String, Any?>> {
		val authRules = mutableSetOf<Map<String, Any?>>()

		for (pathname in openAPI.paths.keys) {
			val ruleMap = mutableMapOf<String, Any?>()
			val pathParts = pathname.split("/")
			val antParts = mutableListOf<String>()
			pathParts.forEach { part ->
				// replace all {name} parts
				antParts.add(if (part.startsWith("{")) "*" else part)
			}
			ruleMap["antMatcher"] = antParts.joinToString("/")

			val pathItem = openAPI.paths[pathname]
			ruleMap["secured"] = findAllParams(pathItem).any { param -> param.name == "bearer" }

			val guards = pathItem?.let { findGuardsInPath(it) }

			guards?.forEach { guard ->
				val alreadyAdded = guardsSet.any { it["guardName"] == guard["guardName"] }
				if (!alreadyAdded) {
					guardsSet.add(guard)
				}
			}

			ruleMap["guards"] = guards
			ruleMap["multiGuards"] = guards != null && guards.size > 1
			ruleMap["hasGuards"] = guards?.isNotEmpty()

			authRules.add(ruleMap)
		}
		return authRules

	}

	private fun findAllParams(path: PathItem?): List<Parameter> {
		val allParams = mutableListOf<Parameter>()
		if (path == null) {
			return allParams
		}
		if (!path.parameters.isNullOrEmpty()) {
			allParams.addAll(path.parameters)
		}
		if (path.get != null && !path.get.parameters.isNullOrEmpty()) {
			allParams.addAll(path.get.parameters)
		}
		if (path.post != null && !path.post.parameters.isNullOrEmpty()) {
			allParams.addAll(path.post.parameters)
		}
		if (path.put != null && !path.put.parameters.isNullOrEmpty()) {
			allParams.addAll(path.put.parameters)
		}
		if (path.delete != null && !path.delete.parameters.isNullOrEmpty()) {
			allParams.addAll(path.delete.parameters)
		}
		return allParams
	}

	private val isReferenceGuardV2 = resolveReferenceGuardV2()

	private fun resolveReferenceGuardV2(): Boolean {
		val security = additionalProperties["security"] as? Map<*, *> ?: return false
		val guards = security["guards"] as? Map<*, *> ?: return false
		val referenceGuard = guards["referenceGuard"] as? Map<*, *> ?: return false
		return referenceGuard["v2"] as? Boolean ?: false
	}

	private fun findGuardsInPath(path: PathItem): List<Map<String, Any?>> {
		val guards = mutableListOf<Map<String, Any?>>()
		val allParams = findAllParams(path)

		if (allParams.isEmpty()) {
			return guards
		}

		for (param in allParams) {
			val isAuthParam = param.`in` == "header" && param.extensions != null
					&& param.extensions.containsKey("x-auth-access")
					&& param.extensions["x-auth-access"].toString().isNotEmpty()
			if (!isAuthParam) {
				continue
			}
			val authFormatKey = "x-auth-format"
			val authFormat = if (param.extensions.containsKey(authFormatKey)) {
				readFormatAndFix(param.extensions[authFormatKey].toString())
			} else null
			if (authFormat.isNullOrEmpty()) {
				//Auth format is required to add Guard
				continue
			}
			val guardName = when(param.name) {
				"X-access-reference" -> "referenceGuard"
				else -> "headerGuard"
			}
			val guardArgs = when(guardName) {
				"referenceGuard" -> if (isReferenceGuardV2) {
					listOf("authentication", "request", "'${param.name}'", "'$authFormat'")
				} else {
					listOf("request", "'${param.name}'", "'$authFormat'")
				}
				"headerGuard" ->  listOf("authentication", "request", "'${param.name}'", "'$authFormat'")
				else -> null
			}
			if (guards.any { it["guardName"] == guardName }) {
				continue
			}
			guards.add(mapOf(
				"headerName" to param.name,
				"guardName" to guardName,
				"guardClassName" to guardName.capitalize(),
				"format" to authFormat,
				"args" to guardArgs
			))
		}
		return guards
	}

	private fun readFormatAndFix(format: String?): String? {
		if (format == null) return null
		return format.split(".").last().removeSuffix("[]")
	}

	private fun setupServerPort(openAPI: OpenAPI) {
		additionalProperties["serverPort"] = ServerPortReader.findPort(openAPI)
	}
}
