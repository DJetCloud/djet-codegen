package pro.bilous.codegen.process

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class OpenApiWrapperTest {
	companion object {
		const val TEST_TYPE = "Test"
	}

	@Test
	fun `should return false for checking and null for finding if openApi is absent`() {
		val codegen = CodeCodegen()
		val wrapper = OpenApiWrapper(codegen)
		assertFalse(wrapper.isOpenApiContainsType(TEST_TYPE))
		assertNull(wrapper.findSchema(TEST_TYPE))
	}

	@Test
	fun `should return false for checking and null for finding if components is absent`() {
		val codegen = CodeCodegen().apply {
			setOpenAPI(OpenAPI())
		}
		val wrapper = OpenApiWrapper(codegen)
		assertFalse(wrapper.isOpenApiContainsType(TEST_TYPE))
		assertNull(wrapper.findSchema(TEST_TYPE))
	}

	@Test
	fun `should return false for checking and null for finding if schemas is absent`() {
		val openAPI = OpenAPI().apply {
			components = Components()
		}
		val codegen = CodeCodegen().apply {
			setOpenAPI(openAPI)
		}
		val wrapper = OpenApiWrapper(codegen)
		assertFalse(wrapper.isOpenApiContainsType(TEST_TYPE))
		assertNull(wrapper.findSchema(TEST_TYPE))
	}

	@Test
	fun `should return false false for checking and null for finding type is not found`() {
		val openAPI = OpenAPI().apply {
			components = Components().apply {
				addSchemas("Int", null)
			}
		}
		val codegen = CodeCodegen().apply {
			setOpenAPI(openAPI)
		}
		val wrapper = OpenApiWrapper(codegen)
		assertFalse(wrapper.isOpenApiContainsType(TEST_TYPE))
		assertNull(wrapper.findSchema(TEST_TYPE))
	}

	@Test
	fun `should return true if type is found`() {
		val openAPI = OpenAPI().apply {
			components = Components().apply {
				addSchemas(TEST_TYPE, null)
			}
		}
		val codegen = CodeCodegen().apply {
			setOpenAPI(openAPI)
		}
		val wrapper = OpenApiWrapper(codegen)
		assertTrue(wrapper.isOpenApiContainsType(TEST_TYPE))
	}

	@Test
	fun `should return schema of the type if type is present`() {
		val schema = Schema<Int>()
		val openAPI = OpenAPI().apply {
			components = Components().apply {
				addSchemas(TEST_TYPE, schema)
			}
		}
		val codegen = CodeCodegen().apply {
			setOpenAPI(openAPI)
		}
		val wrapper = OpenApiWrapper(codegen)
		assertEquals(schema, wrapper.findSchema(TEST_TYPE))
	}

}
