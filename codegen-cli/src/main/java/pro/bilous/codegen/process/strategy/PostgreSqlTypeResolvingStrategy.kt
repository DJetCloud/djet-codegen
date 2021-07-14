package pro.bilous.codegen.process.strategy

object PostgreSqlTypeResolvingStrategy : DefaultTypeResolvingStrategy() {

	private val DATA_TYPES = mapOf(
		"VARCHAR" to ColumnTypePare("\${VARCHAR_OBJECT}", "varchar"),
		"TEXT" to ColumnTypePare("\${TEXT_TYPE}", "text"),
	)

	private const val MAX_SIZE_FOR_VARCHAR = 10485760

	override fun resolveStringTypeWithSize(size: Int): ColumnTypePare {
		return if (size <= MAX_SIZE_FOR_VARCHAR) {
			ColumnTypePare("VARCHAR(${size})", null)
		} else {
			DATA_TYPES["TEXT"]!!
		}
	}

	override fun resolveStringTypeWithFormat(format: String): ColumnTypePare? {
		val columnType = format.trim().toUpperCase()
		return DATA_TYPES[columnType]
	}
}
