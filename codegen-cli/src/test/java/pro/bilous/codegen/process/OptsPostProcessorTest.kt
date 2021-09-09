package pro.bilous.codegen.process

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import org.junit.Assert.*
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.SupportingFile
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class OptsPostProcessorTest {
	@Test
	fun `check if _gitignore is added to the files to be generated`() {
		val shouldBeGenerated = SupportingFile(
			"raw/_gitignore",
			".gitignore"
		)
		val codegen = CodeCodegen()
		val processor = OptsPostProcessor(codegen)
		processor.processOpts()
		val codegenSupportingFiles = codegen.supportingFiles()
		assertNotNull(codegenSupportingFiles?.find { it.equals(shouldBeGenerated) })
	}

	@Test
	fun `check if _editorconfig is added to the files to be generated`() {
		val shouldBeGenerated = SupportingFile(
			"raw/_editorconfig",
			".editorconfig"
		)
		val codegen = CodeCodegen()
		val processor = OptsPostProcessor(codegen)
		processor.processOpts()
		val codegenSupportingFiles = codegen.supportingFiles()
		assertNotNull(codegenSupportingFiles?.find { it.equals(shouldBeGenerated) })
	}

	@Test
	fun `check if _gradle_properties is added to the files to be generated`() {
		val shouldBeGenerated = SupportingFile(
			"raw/_gradle_properties",
			"gradle.properties"
		)
		val codegen = CodeCodegen()
		val processor = OptsPostProcessor(codegen)
		processor.processOpts()
		val codegenSupportingFiles = codegen.supportingFiles()
		assertNotNull(codegenSupportingFiles?.find { it.equals(shouldBeGenerated) })
	}

	@Test
	fun `check if idea files is added to the files to be generated`() {
		val shouldBeGenerated = SupportingFile(
			"idea/runConfiguration.mustache",
			".idea/runConfigurations/Application.xml"
		)
		val codegen = CodeCodegen()
		val processor = OptsPostProcessor(codegen)
		processor.processOpts()
		val codegenSupportingFiles = codegen.supportingFiles()
		assertNotNull(codegenSupportingFiles?.find { it.equals(shouldBeGenerated) })
	}

	@Test
	fun `should add enum definitions files to supported files`() {
		val codegen: CodeCodegen = CodeCodegen().apply {
			artifactId = "test"
		}

		val openAPI = OpenAPI()
		openAPI.components = Components().apply { schemas = emptyMap() }
		codegen.setOpenAPI(openAPI)

		val additionalPropertiesField =
			CodeCodegen::class.java.superclass.superclass.declaredFields.find { it.name == "additionalProperties" }
		additionalPropertiesField?.isAccessible = true
		val additionalProperties = mapOf(CodeCodegen.BASE_PACKAGE to "Test", "enumsType" to "MetadataEnums")
		additionalPropertiesField?.set(codegen, additionalProperties)

		val optsPostProcessor = OptsPostProcessor(codegen)
		optsPostProcessor.processOpts()

		val enumValidationTemplateFile = codegen.supportingFiles()
			.find { it.templateFile == "common/src/main/kotlin//config/EnumValidationConfiguration.kt.mustache" }
		kotlin.test.assertNotNull(enumValidationTemplateFile)
	}
}

