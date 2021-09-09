package pro.bilous.codegen.core

import io.swagger.v3.oas.models.Paths
import org.openapitools.codegen.*
import org.openapitools.codegen.api.TemplatePathLocator
import org.openapitools.codegen.templating.CommonTemplateContentLocator
import org.openapitools.codegen.templating.MustacheEngineAdapter
import org.openapitools.codegen.templating.TemplateManagerOptions
import org.slf4j.LoggerFactory
import pro.bilous.codegen.process.deployment.DeploymentFileResolver
import pro.bilous.codegen.process.filename.FilePathResolver
import java.io.File
import java.io.IOException

open class InCodeGenerator : DefaultGenerator() {

	companion object {
		private val log = LoggerFactory.getLogger(InCodeGenerator::class.java)
	}

	var codegen: CodeCodegen? = null
	private val deployment = DeploymentFileResolver()

	/**
	 * This is the final point to override things right before the processing templates and writing file to filesystem.
	 * All params can be changed before this final stage (pass to super to execute the stage)
	 * @param templateData – map of data to process
	 * @param templateName – name of the template to pick up for the processing
	 * @param outputFilename – target full name of the file
	 * @return file written to the filesystem
	 */
	@Throws(IOException::class)
	override fun processTemplateToFile(templateData: Map<String, Any>,
									   templateName: String,
									   outputFilename: String,
									   shouldGenerate: Boolean,
									   skippedByOption: String): File? {
		log.debug("processTemplateToFile, templateName: $templateName, outputFilename: $outputFilename")

		var lastFile: File? = null
		if (templateName == "kube/\$env/configmap.yml.mustache") {
			val list = deployment.resolveDeployment(templateData, templateName, outputFilename)
			list.forEach { deploymentItem ->
				lastFile = super.processTemplateToFile(
					deploymentItem.templateData,
					templateName,
					deploymentItem.filePath,
					shouldGenerate,
					skippedByOption
				)
			}
			return lastFile
		}

		val target = FilePathResolver().resolve(templateData, templateName, outputFilename)
		return if (target.shouldWriteFile) {
			super.processTemplateToFile(templateData, templateName, target.targetPath, shouldGenerate, skippedByOption)
		} else null
	}

	override fun opts(opts: ClientOptInput): Generator {
		codegen = opts.config as CodeCodegen
		val generator = super.opts(opts)

		// should override TemplateManager to apply custom behaviour for file writes
		if (templateProcessor is TemplateManager) {
			templateProcessor = createTemplateProcessorWrapper(codegen!!)
		}
		return generator
	}

	private fun createTemplateProcessorWrapper(codegen: CodeCodegen): TemplateManager {
		val templateManagerOptions = TemplateManagerOptions(config.isEnableMinimalUpdate, config.isSkipOverwrite)
		val templatingEngine = config.templatingEngine

		if (templatingEngine is MustacheEngineAdapter) {
			templatingEngine.compiler = config.processCompiler(templatingEngine.compiler)
		}
		val commonTemplateLocator: TemplatePathLocator = CommonTemplateContentLocator()
		val generatorTemplateLocator: TemplatePathLocator = DjetTemplateContentLocator(config)
		return DjetTemplateManager(
			codegen,
			templateManagerOptions,
			templatingEngine,
			arrayOf(generatorTemplateLocator, commonTemplateLocator)
		)
	}

	override fun processPaths(paths: Paths?): MutableMap<String, MutableList<CodegenOperation>> {
		val paths =  super.processPaths(paths)
		// ignore paths permanently
		if (paths.containsKey("Entities")) {
			paths.remove("Entities")
		}
		return paths
	}
}
