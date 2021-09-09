package pro.bilous.intellij.plugin

import java.io.File
import java.lang.IllegalStateException

object PathTools {
	private const val HOME_PATH = "djet"
	private const val CREDENTIALS_FILE = ".credentials"
	private const val SETTINGS_FILE = "settings"

	fun getHomePath(projectPath: String?): String {
		if (projectPath == null) {
			throw IllegalStateException("projectPath can not be null")
		}

		resolveHomePathForOldProjects(projectPath)

		return "$projectPath/$HOME_PATH"
	}

	fun getCredentialsPath(projectPath: String?): String {
		return "${getHomePath(projectPath)}/$CREDENTIALS_FILE.yaml"
	}

	fun getSettingsPath(projectPath: String?): String {
		return "${getHomePath(projectPath)}/$SETTINGS_FILE.yaml"
	}

	private fun resolveHomePathForOldProjects(projectPath: String) {
		val newHomeFolder = File("$projectPath/$HOME_PATH")
		if (newHomeFolder.exists()) {
			return
		}
		val oldHomePath = "$projectPath/.difhub-codegen"
		val oldHomeFolder = File(oldHomePath)
		if (oldHomeFolder.exists()) {
			oldHomeFolder.renameTo(newHomeFolder)
		}
	}
}
