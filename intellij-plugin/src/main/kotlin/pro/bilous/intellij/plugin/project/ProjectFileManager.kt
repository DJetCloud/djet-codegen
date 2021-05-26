package pro.bilous.intellij.plugin.project

import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import pro.bilous.difhub.config.DatasetStatus
import java.lang.IllegalStateException

class ProjectFileManager {

	private val fileCreator = ProjectFilesCreator()

	fun createAndOpenProjectCredentials(configFolder: String, project: Project) {
		val request = ProjectCreationRequest().apply {
			username = "add DifHub username"
			password = "add DifHub password"
			organization = "add DifHub organization name"
		}
		fileCreator.createCredentialsFile(request, configFolder)

		val filePath = "${configFolder}/.credentials.yaml"
		val file = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://$filePath")
			?: throw IllegalStateException("Missing project credentials file, location: $filePath")
		OpenFileDescriptor(project, file).navigate(true)
	}

	fun createAndOpenProjectSettings(configFolder: String, project: Project) {
		val request = ProjectCreationRequest().apply {
			system = "add DifHub system"
			applications = mutableSetOf("add system application")
			groupId = "add group id"
			artifactId = "add artifact id"
			version = "add version"
			description = "add description"
			title = "add title"
			basePackage = "add base package"
			dbName = "add db name"
			database = "MySQL"
			defaultStringSize = "255"
			addKotlin = true
			dateLibrary = "default"
			addBindingEntity = true
			datasetStatus = DatasetStatus.APPROVED
		}
		fileCreator.createConfigFile(request, configFolder)

		val filePath = "${configFolder}/settings.yaml"
		val file = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://$filePath")
			?: throw IllegalStateException("Missing project properties file, location: $filePath")
		OpenFileDescriptor(project, file).navigate(true)
	}
}
