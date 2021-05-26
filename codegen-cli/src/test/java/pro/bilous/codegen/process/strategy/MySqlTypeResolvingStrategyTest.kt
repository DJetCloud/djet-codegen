package pro.bilous.codegen.process.strategy

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenProperty

class MySqlTypeResolvingStrategyTest {

	companion object {
		const val MAX_SIZE_FOR_VARCHAR = 21844
		const val DEFAULT_STRING_SIZE = 700
		const val PROPERTY_MAX_LENGTH = 333
		const val UNDEFINED_PROPERTY_TYPE = "undefined property type"
	}

	@Test
	@DisplayName("Should assign VARCHAR with size of property.maxLength")
	fun `should assign VARCHAR with property maxLength`() {
		val property = getProperty()
		property.maxLength = PROPERTY_MAX_LENGTH

		MySqlTypeResolvingStrategy.resolvePropertyType(property, DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${PROPERTY_MAX_LENGTH})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName("Should assign TEXT if property.maxLength is greter then MAX_SIZE_FOR_VARCHAR")
	fun `should assign TEXT if property maxLength is greater then MAX_SIZE_FOR_VARCHAR`() {
		val property = getProperty()
		property.maxLength = MAX_SIZE_FOR_VARCHAR * 2

		MySqlTypeResolvingStrategy.resolvePropertyType(property, DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("TEXT", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type VARCHAR with default size if property datatype is undefined" +
				"and property.maxLength is undefined"
	)
	fun `should assign VARCHAR with default string size`() {
		val property = getProperty()

		MySqlTypeResolvingStrategy.resolvePropertyType(property, DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${DEFAULT_STRING_SIZE})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type TEXT if property datatype, property.maxLength are undefined" +
				"and default size is greater then MAX_SIZE_FOR_VARCHAR"
	)
	fun `should assign TEXT when default string size is greater then MAX_SIZE_FOR_VARCHAR`() {
		val property = getProperty()

		MySqlTypeResolvingStrategy.resolvePropertyType(property, MAX_SIZE_FOR_VARCHAR * 2)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("TEXT", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type TINYBLOB if property datatype, property.maxLength are undefined" +
				"and x-format is defined to TINYBLOB"
	)
	fun `should assign TINYBLOB when x-format is TINYBLOB`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "TINYBLOB"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("TINYBLOB", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type TEXT if property datatype, property.maxLength are undefined" +
				"and x-format is defined to TEXT"
	)
	fun `should assign TEXT when x-format is TEXT`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "TEXT"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("TEXT", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type TINYTEXT if property datatype, property.maxLength are undefined" +
				"and x-format is defined to TINYTEXT"
	)
	fun `should assign TINYTEXT when x-format is TINYTEXT`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "TINYTEXT"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("TINYTEXT", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type BLOB if property datatype, property.maxLength are undefined" +
				"and x-format is defined to BLOB"
	)
	fun `should assign BLOB when x-format is BLOB`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "BLOB"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("BLOB", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type MEDIUMBLOB if property datatype, property.maxLength are undefined" +
				"and x-format is defined to MEDIUMBLOB"
	)
	fun `should assign MEDIUMBLOB when x-format is MEDIUMBLOB`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "MEDIUMBLOB"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("MEDIUMBLOB", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type MEDIUMTEXT if property datatype, property.maxLength are undefined" +
				"and x-format is defined to MEDIUMTEXT"
	)
	fun `should assign MEDIUMTEXT when x-format is MEDIUMTEXT`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "MEDIUMTEXT"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("MEDIUMTEXT", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type LONGBLOB if property datatype, property.maxLength are undefined" +
				"and x-format is defined to LONGBLOB"
	)
	fun `should assign LONGBLOB when x-format is LONGBLOB`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "LONGBLOB"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("LONGBLOB", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type LONGTEXT if property datatype, property.maxLength are undefined" +
				"and x-format is defined to LONGTEXT"
	)
	fun `should assign LONGTEXT when x-format is LONGTEXT`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "LONGTEXT"

		MySqlTypeResolvingStrategy.resolvePropertyType(property, null)

		kotlin.test.assertEquals("LONGTEXT", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	private fun getProperty(): CodegenProperty {
		val property = CodegenProperty()
		property.datatypeWithEnum = UNDEFINED_PROPERTY_TYPE
		return property
	}
}
