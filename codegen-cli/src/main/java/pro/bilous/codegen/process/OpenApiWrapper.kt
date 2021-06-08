package pro.bilous.codegen.process

import io.swagger.v3.oas.models.media.Schema
import org.openapitools.codegen.CodeCodegen

interface IOpenApiWrapper {
	fun isOpenApiContainsType(complexType: String?): Boolean
	fun findSchema(complexType: String?): Schema<*>?
}

open class OpenApiWrapper(val codegen: CodeCodegen) : IOpenApiWrapper {
	override fun isOpenApiContainsType(complexType: String?): Boolean {
		return codegen.getOpenApi()?.components?.schemas?.containsKey(complexType) ?: false
	}

	override fun findSchema(complexType: String?): Schema<*>? {
		return codegen.getOpenApi()?.components?.schemas?.let { it[complexType] }
	}
}
