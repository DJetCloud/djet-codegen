package pro.bilous.codegen.core

import org.openapitools.codegen.CodegenConfig
import org.openapitools.codegen.templating.GeneratorTemplateContentLocator
import java.io.File

class DjetTemplateContentLocator(private val codegenConfig: CodegenConfig?) : GeneratorTemplateContentLocator(codegenConfig) {

	override fun getFullTemplatePath(relativeTemplateFile: String): String? {
		val superPath = super.getFullTemplatePath(relativeTemplateFile)

		if (relativeTemplateFile.contains("generatedAnnotation")) {
			println()
		}

		if (!superPath.isNullOrEmpty()) {
			return superPath
		}
		val embedPath = codegenConfig!!.templateDir() + File.separator + "embed" + File.separator + relativeTemplateFile
		if (embeddedTemplateExists(embedPath)) {
			return embedPath
		}
		return null
	}
}
