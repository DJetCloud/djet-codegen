package pro.bilous.intellij.plugin

import io.swagger.util.Yaml
import org.junit.jupiter.api.Test
import pro.bilous.difhub.write.YamlWriter
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class MigrationsTest {

	companion object {
		const val USERNAME = "test@username"
		const val PASSWORD = "test password"
		const val ORGANIZATION = "test organization"
		const val SYSTEM = "test system"
	}

    @Test
    fun `should move property Organization from credentials to settings`() {
		val projectPath = Paths.get("build/tmp").toAbsolutePath().toString()
		val homePath = PathTools.getHomePath(projectPath)

		val credentialsContent = mapOf(
			"username" to USERNAME,
			"password" to PASSWORD,
			"organization" to ORGANIZATION
		)
		YamlWriter("").writeFile(credentialsContent, homePath, ".credentials")

		val settingsContent = mapOf(
			"system" to SYSTEM
		)
		YamlWriter("").writeFile(settingsContent, homePath, "settings")

		Migrations.movePropertyOrganizationFromCredentialsToSettings(projectPath)

		val credentialsFile = File("$homePath/.credentials.yaml")
		val credentialsJson = Yaml.mapper().readTree(credentialsFile.inputStream())

		val settingsFile = File("$homePath/settings.yaml")
		val settingsJson = Yaml.mapper().readTree(settingsFile.inputStream())

		assertNull(credentialsJson["organization"])
		assertEquals(ORGANIZATION, settingsJson["organization"]?.asText())
    }

	@Test
	fun `should do nothing if any file is absent`() {
		val projectPath = Paths.get("build/tmp").toAbsolutePath().toString()
		val homePath = PathTools.getHomePath(projectPath)
		val credentialsFile = File("$homePath/.credentials.yaml")
		val settingsFile = File("$homePath/settings.yaml")

		if (credentialsFile.exists())  {
			credentialsFile.delete()
		}
		if (settingsFile.exists()) {
			settingsFile.delete()
		}

		Migrations.movePropertyOrganizationFromCredentialsToSettings(projectPath)

		assertFalse(credentialsFile.exists())
		assertFalse(settingsFile.exists())
	}
}
