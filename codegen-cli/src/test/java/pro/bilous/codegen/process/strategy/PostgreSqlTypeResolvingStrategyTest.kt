package pro.bilous.codegen.process.strategy

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenProperty

class PostgreSqlTypeResolvingStrategyTest {

	companion object {
		const val DBASE_NAME = "postgresql"
		const val MAX_SIZE_FOR_VARCHAR = 10485760
		const val DEFAULT_STRING_SIZE = 700
		const val PROPERTY_MAX_LENGTH = 333
	}

	@Test
	@DisplayName("Should assign VARCHAR with size of property.maxLength")
	fun `should assign VARCHAR with property maxLength`() {
		val property = getProperty()
		property.maxLength = PROPERTY_MAX_LENGTH

		PostgreSqlTypeResolvingStrategy.resolve(DBASE_NAME, property, DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${PROPERTY_MAX_LENGTH})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName("Should assign TEXT if property.maxLength is greter then MAX_SIZE_FOR_VARCHAR")
	fun `should assign TEXT if property maxLength is greater then MAX_SIZE_FOR_VARCHAR`() {
		val property = getProperty()
		property.maxLength = MAX_SIZE_FOR_VARCHAR * 2

		PostgreSqlTypeResolvingStrategy.resolve(DBASE_NAME, property, DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("\${TEXT_TYPE}", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type VARCHAR(DEFAULT_STRING_SIZE) if property datatype is undefined" +
				"and property.maxLength is undefined"
	)
	fun `should assign VARCHAR with default string size`() {
		val property = getProperty()

		PostgreSqlTypeResolvingStrategy.resolve(DBASE_NAME, property, DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${DEFAULT_STRING_SIZE})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type VARCHAR if property datatype, property.maxLength are undefined" +
				"and x-format is defined to TINYBLOB"
	)
	fun `should assign VARCHAR when x-format is VARCHAR`() {
		val property = getProperty()
		val ve = property.vendorExtensions
		ve["x-format"] = "VARCHAR"

		PostgreSqlTypeResolvingStrategy.resolve(DBASE_NAME, property, null)

		kotlin.test.assertEquals("\${VARCHAR_OBJECT}", ve["columnType"])
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

		PostgreSqlTypeResolvingStrategy.resolve(DBASE_NAME, property, null)

		kotlin.test.assertEquals("\${TEXT_TYPE}", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	private fun getProperty(): CodegenProperty {
		val property = CodegenProperty()
		property.datatypeWithEnum = MySqlTypeResolvingStrategyTest.UNDEFINED_PROPERTY_TYPE
		return property
	}
}
