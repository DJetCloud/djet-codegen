package pro.bilous.codegen.process.strategy

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals

class DefaultTypeResolvingStrategyTest {

	companion object {
		const val PROJECT_DEFAULT_STRING_SIZE = 700
		const val STRING_SIZE_WHEN_USAGE_IS_DESCRIPTION = 4096
		const val DEFAULT_STRING_SIZE = 255
		const val PROPERTY_MAX_LENGTH = 333
		const val UNDEFINED_PROPERTY_TYPE = "undefined property type"
	}

	@Test
	@DisplayName("Should assign type \${BOOLEAN_VALUE} if property.datatypeWithEnum == Boolean")
	fun `should assign Boolean if property datatype is Boolean`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Boolean"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("\${BOOLEAN_VALUE}", ve["columnType"])
		kotlin.test.assertEquals("java.lang.Boolean", ve["hibernateType"])
		kotlin.test.assertTrue(property.isBoolean)
	}

	@Test
	@DisplayName("Should assign type \${BOOLEAN_VALUE} if property.datatypeWithEnum == Boolean?")
	fun `should assign Boolean if property datatype is Boolean?`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Boolean?"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("\${BOOLEAN_VALUE}", ve["columnType"])
		kotlin.test.assertEquals("java.lang.Boolean", ve["hibernateType"])
		kotlin.test.assertTrue(property.isBoolean)
	}

	@Test
	@DisplayName("Should assign type 'datetime' if property.datatypeWithEnum == Date")
	fun `should assign datetime if property datatype is Date`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Date"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("datetime", ve["columnType"])
		kotlin.test.assertEquals("java.util.Date", ve["hibernateType"])
		kotlin.test.assertTrue(property.isDate)
	}

	@Test
	@DisplayName("Should assign type 'datetime' if property.datatypeWithEnum == Date?")
	fun `should assign datetime if property datatype is nullable Date`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Date?"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("datetime", ve["columnType"])
		kotlin.test.assertEquals("java.util.Date", ve["hibernateType"])
		kotlin.test.assertTrue(property.isDate)
	}

	@Test
	@DisplayName("Should assign type 'int' if property.datatypeWithEnum == Int")
	fun `should assign int if property datatype is Int`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Int"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("int", ve["columnType"])
		kotlin.test.assertTrue(property.isInteger)
	}

	@Test
	@DisplayName("Should assign type 'int' if property.datatypeWithEnum == Int?")
	fun `should assign int if property datatype is nullable Int`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Int?"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("int", ve["columnType"])
		kotlin.test.assertTrue(property.isInteger)
	}

	@Test
	@DisplayName("Should assign type 'decimal(10,2)' if property.datatypeWithEnum == BigDecimal")
	fun `should assign decimal(10,2) if property datatype is BigDecimal`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "BigDecimal"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("decimal(10,2)", ve["columnType"])
		kotlin.test.assertTrue(property.isNumber)
	}

	@Test
	@DisplayName("Should assign type 'decimal(10,2)' if property.datatypeWithEnum == BigDecimal?")
	fun `should assign decimal(10,2) if property datatype is nullable BigDecimal`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "BigDecimal?"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("decimal(10,2)", ve["columnType"])
		kotlin.test.assertTrue(property.isNumber)
	}

	@Test
	@DisplayName("Should assign type 'decimal(10,2)' if property.datatypeWithEnum == Long")
	fun `should assign bigint if property datatype is Long`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Long"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("bigint", ve["columnType"])
		kotlin.test.assertTrue(property.isNumber)
	}

	@Test
	@DisplayName("Should assign type 'decimal(10,2)' if property.datatypeWithEnum == Long?")
	fun `should assign bigint if property datatype is nullable Long`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = "Long?"
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("bigint", ve["columnType"])
		kotlin.test.assertTrue(property.isNumber)
	}

	@Test
	@DisplayName("Should assign type 'VARCHAR(PROPERTY_MAX_LENGTH)' if property.datatypeWithEnum is undefined")
	fun `should assign VARCHAR with size of property maxLength when property datatype is undefined`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = UNDEFINED_PROPERTY_TYPE
		property.maxLength = PROPERTY_MAX_LENGTH
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${PROPERTY_MAX_LENGTH})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName("Should assign type 'VARCHAR(DEFAULT_STRING_SIZE)' if property.datatypeWithEnum is undefined")
	fun `should assign VARCHAR if property datatype and maxLength are undefined and defaultSize presents`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = UNDEFINED_PROPERTY_TYPE
		resolvingStrategy.resolvePropertyType(property, PROJECT_DEFAULT_STRING_SIZE)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${PROJECT_DEFAULT_STRING_SIZE})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName(
		"Should assign type 'VARCHAR(STRING_SIZE_WHEN_USAGE_IS_DESCRIPTION)' for usage == 'Description'" +
				" and undefined property type and default size"
	)
	fun `should assign VARCHAR if property datatype, maxLength and default size are undefined and usage is 'Description'`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = UNDEFINED_PROPERTY_TYPE
		property.vendorExtensions["x-usage"] = "Description"
		resolvingStrategy.resolvePropertyType(property, null)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${STRING_SIZE_WHEN_USAGE_IS_DESCRIPTION})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	@DisplayName("Should assign type 'VARCHAR(DEFAULT_STRING_SIZE)' for undefined property type, default size and usage is not 'Description'")
	fun `should assign VARCHAR if property datatype, default size are undefined and usage is not 'Description'`() {
		val resolvingStrategy = DefaultTypeResolvingStrategy()

		val property = CodegenProperty()
		property.datatypeWithEnum = UNDEFINED_PROPERTY_TYPE
		property.vendorExtensions["x-usage"] = "not description"
		resolvingStrategy.resolvePropertyType(property, null)

		val ve = property.vendorExtensions
		kotlin.test.assertEquals("VARCHAR(${DEFAULT_STRING_SIZE})", ve["columnType"])
		kotlin.test.assertEquals("java.lang.String", ve["hibernateType"])
	}

	@Test
	fun `should return text type for x-data-type=text`() {
		val property = CodegenProperty().apply {
			datatypeWithEnum = "String?"
			vendorExtensions["x-data-type"] = "Text"
		}

		DefaultTypeResolvingStrategy().resolvePropertyType(property)

		assertEquals("\${TEXT_TYPE}", property.vendorExtensions["columnType"])
		assertEquals("java.lang.String", property.vendorExtensions["hibernateType"])
		assertEquals("text", property.vendorExtensions["columnDefinition"])
	}
}
