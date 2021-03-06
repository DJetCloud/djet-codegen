package pro.bilous.codegen.process.models

import org.openapitools.codegen.CodegenModel
import pro.bilous.codegen.utils.SuperclassRegistry

class CommonModelsProcessor(val properties: MutableMap<String, Any>) {

	companion object {
		private val commonModelNames = listOf(
			"Identity", "Entity", "History", "Binding", "ResourceEntity", "Element"
		)
	}

	fun process(model: CodegenModel) {
		if (!isCommonModel(model.name)) {
			return
		}
		val commonModels = if (properties.containsKey("commonModels")) {
			properties["commonModels"] as MutableSet<String>
		} else {
			val listToAdd = mutableSetOf(model.name)
			properties["commonModels"] = listToAdd
			listToAdd
		}

		commonModels.add(model.classname)
		model.vars.forEach {
			if (it.complexType != null) {
				commonModels.add(it.complexType)
			}
		}
		fixProperties(model)
	}

	private fun isCommonModel(name: String): Boolean {
		return commonModelNames.contains(name) || SuperclassRegistry.hasName(name)
	}

	private fun fixProperties(model: CodegenModel) {
		if (model.classname == "ResourceEntity") {
			model.vendorExtensions["addEntityIdVar"] = true
			model.vendorExtensions["isEmbeddable"] = false
			model.imports.add("JsonIgnore")
			// The field 'tags' was forbidden, but now it is allowed
			// model.vars.removeIf { it.name == "tags" }
		} else if (model.classname == "Identity") {
			model.vendorExtensions["isEmbeddable"] = true
			model.vendorExtensions["addIdentityId"] = true
			val idVar = model.vars.find { it.name == "id" }
			if (idVar != null) {
				idVar.vendorExtensions["readOnlyColumn"] = true
			}
		}
		// quick fix for the identity and entity
		when (model.classname) {
			"ResourceEntity", "Identity", "History", "BaseResource" -> {
				model.vars.forEach {
					if (it.required) {
						it.required = false
						it.datatypeWithEnum += "?"
					}
					if (it.defaultValue == null && !it.required) {
						it.defaultValue = "null"
					}
					if (it.name == "history") {
						it.required = true
						it.vendorExtensions["isHistory"] = true
						it.defaultValue = "History()"
						it.datatypeWithEnum = "History"
					}
				}
			}
		}
		if (model.classname == "History") {
			val suffixHistoryAt = model.vars.any { it.name == "createdAt" }
			if (suffixHistoryAt) {
				properties["suffixHistoryAt"] = true
			}
		}
	}

	fun canResolveImport(modelName: String): Boolean {
		if (commonModelNames.contains(modelName) || SuperclassRegistry.hasName(modelName)) {
			return true
		}
		if (!properties.containsKey("commonModels")) {
			return false
		}
		return (properties["commonModels"] as Set<String>).contains(modelName)
	}

	fun resolveImport(modelName: String): String {
		val commonModelPackage = "${properties["basePackage"]}.domain"
		return "$commonModelPackage.$modelName"
	}
 }
