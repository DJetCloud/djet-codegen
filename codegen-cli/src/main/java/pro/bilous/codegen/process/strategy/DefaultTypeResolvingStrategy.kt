package pro.bilous.codegen.process.strategy

import org.openapitools.codegen.CodegenProperty

data class ColumnTypePare(val columnType:String, val columnDefinition: String?)

open class DefaultTypeResolvingStrategy {

	companion object {
		const val DEFAULT_STRING_SIZE = 255
		const val USAGE_DESCRIPTION_NAME = "Description"
		const val DEFAULT_SIZE_FOR_DESCRIPTION = 4096
	}

	fun resolvePropertyType(property: CodegenProperty, defaultStringSize: Int?) {
		val resolvedDefaultStringSize =
			if (defaultStringSize != null && defaultStringSize > 0) {
				defaultStringSize
			} else {
				DEFAULT_STRING_SIZE
			}
		when (property.datatypeWithEnum) {
			"Boolean", "Boolean?" -> {
				property.vendorExtensions["columnType"] = "\${BOOLEAN_VALUE}"
				property.vendorExtensions["hibernateType"] = "java.lang.Boolean"
				property.isBoolean = true
			}
			"Date", "Date?" -> {
				property.vendorExtensions["columnType"] = "datetime"
				property.vendorExtensions["hibernateType"] = "java.util.Date"
				property.isDate = true
			}
			"Int", "Int?" -> {
				property.vendorExtensions["columnType"] = "int"
				property.isInteger = true
			}
			"BigDecimal", "BigDecimal?" -> {
				property.vendorExtensions["columnType"] = "decimal(10,2)"
				property.isNumber = true
			}
			"Long", "Long?" -> {
				property.vendorExtensions["columnType"] = "bigint"
				property.isNumber = true
			}
			else -> {
				resolveUndefinedType(property, resolvedDefaultStringSize)
			}
		}
	}

	private fun resolveUndefinedType(property: CodegenProperty, defaultStringSize: Int) {
		val (columnType, columnDefinition) =
			if (property.maxLength != null && property.maxLength > 0) {
				resolveStringTypeWithSize(property.maxLength)
			} else {
				val format = property.vendorExtensions["x-format"]?.let { it as? String }
				format?.let { resolveStringTypeWithFormat(it) } ?: run {
					val usage = property.vendorExtensions["x-usage"]?.let { it as? String }
					if (usage != null && usage == USAGE_DESCRIPTION_NAME) {
						resolveStringTypeWithSize(DEFAULT_SIZE_FOR_DESCRIPTION)
					} else {
						resolveStringTypeWithSize(defaultStringSize)
					}
				}
			}
		property.vendorExtensions["columnType"] = columnType
		if(columnDefinition != null) property.vendorExtensions["columnDefinition"] = columnDefinition
		property.vendorExtensions["hibernateType"] = "java.lang.String"
	}

	protected open fun resolveStringTypeWithSize(size: Int): ColumnTypePare =
		ColumnTypePare("VARCHAR(${size})", null)

	protected open fun resolveStringTypeWithFormat(format: String): ColumnTypePare? = null
}
