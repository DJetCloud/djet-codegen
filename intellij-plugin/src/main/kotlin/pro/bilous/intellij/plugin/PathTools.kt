package pro.bilous.intellij.plugin

import com.intellij.openapi.vfs.VirtualFileManager
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

	fun resolveHomePathForOldProjects(projectPath: String) {
		val oldHomePath = "file://$projectPath/.difhub-codegen"

		val oldHomeFolder = VirtualFileManager.getInstance().findFileByUrl(oldHomePath)
		if (oldHomeFolder != null) {
			oldHomeFolder.rename(null, HOME_PATH)
			VirtualFileManager.getInstance().syncRefresh()
		}
	}
}
