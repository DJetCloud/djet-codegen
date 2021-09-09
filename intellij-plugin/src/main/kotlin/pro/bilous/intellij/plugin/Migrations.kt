package pro.bilous.intellij.plugin

import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.util.Yaml
import pro.bilous.difhub.write.YamlWriter
import java.io.File
import java.io.IOException

object Migrations {
	private const val ANSI_GREEN = "\u001B[32m"

	fun movePropertyOrganizationFromCredentialsToSettings(projectPath: String) {
		val homePath = PathTools.getHomePath(projectPath)
		val credentialsPath = "$homePath/.credentials.yaml"
		val credentials = getPropertiesFromYamlFile(credentialsPath) ?: return
		val organization = credentials["organization"]?.asText() ?: return
		val settingsPath = "$homePath/settings.yaml"
		val settings = getPropertiesFromYamlFile(settingsPath) ?: return
		settings.put("organization", organization)
		credentials.remove("organization")
		val writer = YamlWriter("")
		writer.writeFile(credentials, homePath, ".credentials")
		writer.writeFile(settings, homePath, "settings")
		info("Property `organization` is successfully moved from .credentials.yaml to settings.yaml")
	}

	private fun getPropertiesFromYamlFile(filePath: String): ObjectNode? {
		return try {
			File(filePath).inputStream().use { Yaml.mapper().readTree(it) as? ObjectNode }
		} catch (e: IOException) {
			null
		}
	}

	private fun info(message: String) {
		println("${ANSI_GREEN}Migration: $message$ANSI_GREEN")
	}
}
