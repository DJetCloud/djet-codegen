package pro.bilous.codegen.utils

object SqlNamingUtils {

	private val sqlNamesToEscape = arrayOf(
		"use",
		"open",
		"drop",
		"create",
		"table",
		"rank",
		"system",
		"function",
		"range",
		"from",
		"order",
		"procedure",
		"from",
		"condition"
	)

	private val columnNamesToEscape = sqlNamesToEscape
	fun escapeColumnNameIfNeeded(columnName: String): String {
		return if (columnNamesToEscape.contains(columnName)) "${columnName}_" else columnName
	}

	private val tableNamesToEscape = sqlNamesToEscape
	fun escapeTableNameIfNeeded(tableName: String): String {
		return if (tableNamesToEscape.contains(tableName)) "${tableName}_" else tableName
	}

}
