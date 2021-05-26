package pro.bilous.codegen.process.strategy

object MySqlTypeResolvingStrategy : DefaultTypeResolvingStrategy() {

	private val DATA_TYPES = mapOf(
		"TINYBLOB" to "tinyblob",
		"TEXT" to "text",
		"TINYTEXT" to "tinytext",
		"BLOB" to "blob",
		"MEDIUMBLOB" to "mediumblob",
		"MEDIUMTEXT" to "mediumtext",
		"LONGBLOB" to "longblob",
		"LONGTEXT" to "longtext"
	)

	private const val MAX_SIZE_FOR_VARCHAR = 21844

	override fun resolveStringTypeWithSize(size: Int): ColumnTypePare {
		return if (size <= MAX_SIZE_FOR_VARCHAR) {
			ColumnTypePare("VARCHAR(${size})", null)
		} else {
			ColumnTypePare("TEXT", DATA_TYPES["TEXT"])
		}
	}

	override fun resolveStringTypeWithFormat(format: String): ColumnTypePare? {
		val columnType = format.trim().toUpperCase()
		val columnDefinition = DATA_TYPES[columnType]
		return if (columnDefinition != null) {
			ColumnTypePare(columnType, columnDefinition)
		} else {
			null
		}
	}
}
