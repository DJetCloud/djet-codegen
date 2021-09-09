package pro.bilous.intellij.plugin.action.menu

import com.fasterxml.jackson.databind.node.ObjectNode
import pro.bilous.intellij.plugin.PathTools
import pro.bilous.intellij.plugin.project.ProjectFileManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import io.swagger.util.Yaml
import org.slf4j.LoggerFactory
import pro.bilous.difhub.config.Config
import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.config.DatasetStatus
import pro.bilous.difhub.convert.DifHubToSwaggerConverter
import pro.bilous.difhub.load.DifHubLoader
import pro.bilous.difhub.load.IModelLoader
import pro.bilous.difhub.load.ModelLoader
import pro.bilous.difhub.write.YamlWriter
import java.io.File

class DifHubLoadOpenApiAction : AnAction() {
	private val log = LoggerFactory.getLogger(DifHubToSwaggerConverter::class.java)

	private val fileManager = ProjectFileManager()

    override fun actionPerformed(e: AnActionEvent) {
		val ve = VerifiedEvent(e)
        val project = ve.project
        val projectPath = ve.projectPath

        val configFolder = PathTools.getHomePath(projectPath)

		val (username, password) = loadCredentials(configFolder, project) ?: return
		val (organization, system, datasetStatus) = loadSettings(configFolder, project) ?: return

		val config = ConfigReader.loadConfig(organization).apply {
			this.system = system
			this.datasetStatus = datasetStatus
		}

		val modelLoader = ModelLoader(DifHubLoader(username, password, config))

		createOpenApiFiles(modelLoader, config, configFolder)
		VirtualFileManager.getInstance().syncRefresh()
	}

	private fun loadSettings(configFolder: String, project: Project): Triple<String, String, DatasetStatus>? {
		val configFilePath = "$configFolder/settings.yaml"
		val configFile = File(configFilePath)
		if (!configFile.exists()) {
			fileManager.createAndOpenProjectSettings(configFolder, project)
			return null
		}
		val configTree = configFile.inputStream().use { Yaml.mapper().readTree(it) as? ObjectNode } ?: return null
		val system = configTree.get("system").asText()
		val organization = configTree.get("organization").asText()
		// we use DRAFT datasets if status wasn't specified
		val statusName = configTree.get("datasetStatus")?.asText() ?: "DRAFT"
		val datasetStatus: DatasetStatus = try {
			DatasetStatus.valueOf(statusName.toUpperCase())
		} catch (e: IllegalArgumentException) {
			log.warn("Illegal dataset status of system: `$statusName`. Status is set to `Draft`")
			DatasetStatus.DRAFT
		}
		return Triple(organization, system, datasetStatus)
	}

	private fun loadCredentials(configFolder: String, project: Project): Pair<String, String>? {
		val configFilePath = "file://$configFolder/.credentials.yaml"
		val configFile = VirtualFileManager.getInstance().findFileByUrl(configFilePath)
		if (configFile == null) {
			fileManager.createAndOpenProjectCredentials(configFolder, project)
			return null
		}
		val fileTree = Yaml.mapper().readTree(configFile.inputStream)

		val username = fileTree.get("username").asText()
		val password = fileTree.get("password").asText()
		return Pair(username, password)
	}

	private fun createOpenApiFiles(modelLoader: IModelLoader, config: Config, configFolder: String) {
		DifHubToSwaggerConverter(modelLoader, config).convertAll().forEach {
			YamlWriter(config.system).writeFile(it.openApi, configFolder, "${it.appName.toLowerCase()}-api")
		}
	}
}
