package pro.bilous.intellij.plugin.project

import io.swagger.util.Yaml
import org.junit.jupiter.api.Test
import pro.bilous.difhub.config.DatasetStatus
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

internal class ProjectFilesCreatorTest {

	companion object {
		const val USERNAME = "test@username"
		const val PASSWORD = "test password"
		const val ORGANIZATION = "test organization"
		const val SYSTEM = "test system"
		val APPLICATION = mutableSetOf("first-app", "second-app", "third-app")
		const val ARTIFACT_ID = "test_artifact_id"
		const val GROUP_ID = "test_group_id"
		const val ARTIFACT_VERSION = "test version"
		const val ARTIFACT_DESCRIPTION = "test description"
		const val TITLE = "test title"
		const val BASE_PACKAGE = "test base package"
		const val DB_NAME = "test db"
		const val DATABASE = "MySQL"
		const val ADD_KOTLIN = true
		const val DATE_LIBRARY = "default"
		const val ADD_BINDING_ENTITY = true
		const val AUTHORIZATION_ENABLED = false
		const val DEFAULT_STRING_SIZE = "255"
		val DATASET_STATUS = DatasetStatus.DRAFT
	}

	@Test
	fun `test credentials file creation`() {
		val request = ProjectCreationRequest().apply {
			username = USERNAME
			password = PASSWORD
		}

		val configFolder = Paths.get("build/tmp").toAbsolutePath().toString()

		ProjectFilesCreator().createCredentialsFile(request, configFolder)

		val credentialsFile = File("$configFolder/.credentials.yaml")

		val credentialsTree = Yaml.mapper().readTree(credentialsFile.inputStream())
		val username = credentialsTree.get("username").asText()
		val password = credentialsTree.get("password").asText()

		assertEquals(request.username, username)
		assertEquals(request.password, password)
	}

	@Test
	fun `test config file creation`() {
		val request = ProjectCreationRequest().apply {
			organization = ORGANIZATION
			system = SYSTEM
			applications = APPLICATION
			artifactId = ARTIFACT_ID
			groupId = GROUP_ID
			version = ARTIFACT_VERSION
			description = ARTIFACT_DESCRIPTION
			title = TITLE
			basePackage = BASE_PACKAGE
			dbName = DB_NAME
			database = DATABASE
			addKotlin = ADD_KOTLIN
			dateLibrary = DATE_LIBRARY
			addBindingEntity = ADD_BINDING_ENTITY
			authorizationEnabled = AUTHORIZATION_ENABLED
			defaultStringSize = DEFAULT_STRING_SIZE
			datasetStatus = DATASET_STATUS
		}

		val configFolder = Paths.get("build/tmp").toAbsolutePath().toString()

		ProjectFilesCreator().createConfigFile(request, configFolder)

		val configFile = File("$configFolder/settings.yaml")

		val configTree = Yaml.mapper().readTree(configFile.inputStream())
		val system = configTree.get("system").asText()
		val application = configTree.get("application").asIterable().map { it.asText() }.toMutableSet()
		val groupId = configTree.get("groupId").asText()
		val artifactId = configTree.get("artifactId").asText()
		val artifactVersion = configTree.get("artifactVersion").asText()
		val artifactDescription = configTree.get("artifactDescription").asText()
		val title = configTree.get("title").asText()
		val basePackage = configTree.get("basePackage").asText()
		val dbName = configTree.get("dbName").asText()
		val database = configTree.get("database").asText()
		val defaultStringSize = configTree.get("defaultStringSize").asInt()
		val addKotlin = configTree.get("addKotlin").asBoolean()
		val dateLibrary = configTree.get("dateLibrary").asText()
		val addBindingEntity = configTree.get("addBindingEntity").asBoolean()
		val authorizationEnabled = configTree.get("authorizationEnabled").asBoolean()
		val datasetStatus = configTree.get("datasetStatus").asText()

		assertEquals(SYSTEM, system)
		assertEquals(APPLICATION, application)
		assertEquals(GROUP_ID, groupId)
		assertEquals(ARTIFACT_ID, artifactId)
		assertEquals(ARTIFACT_VERSION, artifactVersion)
		assertEquals(ARTIFACT_DESCRIPTION, artifactDescription)
		assertEquals(TITLE, title)
		assertEquals(BASE_PACKAGE, basePackage)
		assertEquals(DB_NAME, dbName)
		assertEquals(DATABASE, database)
		assertEquals(DEFAULT_STRING_SIZE.toInt(), defaultStringSize)
		assertEquals(ADD_KOTLIN, addKotlin)
		assertEquals(DATE_LIBRARY, dateLibrary)
		assertEquals(ADD_BINDING_ENTITY, addBindingEntity)
		assertEquals(AUTHORIZATION_ENABLED, authorizationEnabled)
		assertEquals(DATASET_STATUS.name.toLowerCase(), datasetStatus)
	}

	@Test
	fun `test settings file creation`() {
		val request = ProjectCreationRequest().apply {
			enumsType = "SimpleEnum"
			database = "Mysql"
		}

		val configFolder = Paths.get("build/tmp").toAbsolutePath().toString()

		ProjectFilesCreator().createConfigFile(request, configFolder)

		val configFile = File("$configFolder/settings.yaml")

		val configTree = Yaml.mapper().readTree(configFile.inputStream())
		val enumsType = configTree.get("enumsType").asText()
		val database = configTree.get("database").asText()

		assertEquals(request.enumsType, enumsType)
		assertEquals(request.database, database)
	}
}
