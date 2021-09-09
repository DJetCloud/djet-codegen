package pro.bilous.codegen.core

import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.TemplateManager
import org.openapitools.codegen.api.TemplatePathLocator
import org.openapitools.codegen.api.TemplatingEngineAdapter
import org.openapitools.codegen.templating.TemplateManagerOptions
import org.slf4j.LoggerFactory
import pro.bilous.codegen.merge.FileMerge
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

class DjetTemplateManager(
	val codegen: CodeCodegen,
	options: TemplateManagerOptions?,
	engineAdapter: TemplatingEngineAdapter?,
	templateLoaders: Array<out TemplatePathLocator>?
) : TemplateManager(options, engineAdapter, templateLoaders) {

	companion object {
		private val log = LoggerFactory.getLogger(DjetTemplateManager::class.java)
	}

	val fileMerge = FileMerge()

	override fun writeToFile(filename: String, contents: ByteArray): File? {
		val newFilename = if (filename.contains("/.openapi-generator/")) {
			filename.replace("/.openapi-generator/", "/djet/")
		} else filename

		// ignore FILES metadata for now, this is not correct for multi OpenAPI projects
		if (newFilename.endsWith("FILES")) {
			return null
		}

		if (codegen.isEnableMerge()) {
			return writeFileWithMerge(newFilename, contents)
		}
		return super.writeToFile(newFilename, contents)
	}

	private fun writeFileWithMerge(filename: String, contents: ByteArray): File {
		val tempFilename = "$filename.tmp"
		// Use Paths.get here to normalize path (for Windows file separator, space escaping on Linux/Mac, etc)
		val outputFile = java.nio.file.Paths.get(filename).toFile()
		var tempFile: File? = null
		try {
			tempFile = writeToFileRaw(tempFilename, contents)
			if (!filesEqual(tempFile, outputFile)) {
				if (outputFile.exists() && fileMerge.supportsMerge(filename)) { // support merge
					mergeFilesAndWriteToTemp(tempFile, outputFile, filename)
				}
				log.info("writing file $filename")
				Files.move(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
				tempFile = null
			} else {
				log.info("skipping unchanged file $filename")
			}
		} finally {
			if (tempFile != null && tempFile.exists()) {
				try {
					tempFile.delete()
				} catch (ex: Exception) {
					log.error("Error removing temporary file $tempFile", ex)
				}
			}
		}
		return outputFile
	}

	private fun mergeFilesAndWriteToTemp(tempFile: File, outputFile: File, filename: String) {
		val tempContent = tempFile.readText()
		val outputContent = outputFile.readText()
		val contentResult = fileMerge.mergeFileContent(outputContent, tempContent, filename)
		writeToFile(tempFile.path, contentResult)
	}

	@Throws(IOException::class)
	private fun writeToFileRaw(filename: String, contents: ByteArray): File {
		// Use Paths.get here to normalize path (for Windows file separator, space escaping on Linux/Mac, etc)
		val output = java.nio.file.Paths.get(filename).toFile()
		if (output.parent != null && !File(output.parent).exists()) {
			val parent = java.nio.file.Paths.get(output.parent).toFile()
			parent.mkdirs()
		}
		Files.write(output.toPath(), contents)
		return output
	}

	@Throws(IOException::class)
	private fun filesEqual(file1: File, file2: File): Boolean {
		return file1.exists() && file2.exists() && Arrays.equals(
			Files.readAllBytes(file1.toPath()),
			Files.readAllBytes(file2.toPath())
		)
	}
}
