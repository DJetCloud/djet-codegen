package pro.bilous.codegen.process.strategy

object MySqlTypeResolvingStrategy : DefaultTypeResolvingStrategy() {

	private val DATA_TYPES = mapOf(
		"TINYBLOB" to ColumnTypePare("\${TINYBLOB_OBJECT}", "tinyblob"),
		"TEXT" to ColumnTypePare("\${TEXT_TYPE}", "text"),
		"TINYTEXT" to ColumnTypePare("\${TINYTEXT_OBJECT}", "tinytext"),
		"BLOB" to ColumnTypePare("\${BLOB_OBJECT}", "blob"),
		"MEDIUMBLOB" to ColumnTypePare("\${MEDIUMBLOB_OBJECT}", "mediumblob"),
		"MEDIUMTEXT" to ColumnTypePare("\${MEDIUMTEXT_OBJECT}", "mediumtext"),
		"LONGBLOB" to ColumnTypePare("\${LONGBLOB_OBJECT}", "longblob"),
		"LONGTEXT" to ColumnTypePare("\${LONGTEXT_OBJECT}", "longtext")
	)

	private const val MAX_SIZE_FOR_VARCHAR = 21844

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
