package pro.bilous.intellij.plugin.project

import pro.bilous.intellij.plugin.PathTools
import pro.bilous.intellij.plugin.gen.CodeGenerator
import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VirtualFileManager
import org.slf4j.LoggerFactory
import pro.bilous.difhub.config.Config
import pro.bilous.difhub.convert.DifHubToSwaggerConverter
import pro.bilous.difhub.load.IModelLoader
import pro.bilous.difhub.write.YamlWriter
import java.lang.Exception

class ProjectFilesCreator {
    private val log = LoggerFactory.getLogger(ProjectFilesCreator::class.java)

    fun createFiles(modelLoader: IModelLoader, config: Config, module: Module, request: ProjectCreationRequest) {
        val project = module.project
        val basePath = project.basePath

        val configFolder = PathTools.getHomePath(basePath)

        createCredentialsFile(request, configFolder)
        createOpenApiFiles(modelLoader, config, request, configFolder)
        createConfigFile(request, configFolder)
        executeCodeGenerator(basePath!!)

        VirtualFileManager.getInstance().syncRefresh()
    }

	fun createCredentialsFile(request: ProjectCreationRequest, configFolder: String) {
        val fileContent = mapOf(
            "username" to request.username,
            "password" to request.password
        )
        YamlWriter(request.system).writeFile(fileContent, configFolder, ".credentials")
    }

    private fun createOpenApiFiles(modelLoader: IModelLoader, config: Config, request: ProjectCreationRequest, configFolder: String) {
        DifHubToSwaggerConverter(modelLoader, config).convertAll().forEach {
            try {
                YamlWriter(request.system).writeFile(it.openApi, configFolder, "${it.appName.toLowerCase()}-api")
            } catch (error: Exception) {
                log.error("Failed system generation $it.appName", error)
            }
        }
    }

	fun createConfigFile(request: ProjectCreationRequest, configFolder: String) {
        val configMap = mapOf(
			"organization" to request.organization,
            "system" to request.system,
            "application" to request.applications,
            "groupId" to request.groupId,
            "artifactId" to request.artifactId,
            "artifactVersion" to request.version,
            "artifactDescription" to request.description,
            "title" to request.title,
            "basePackage" to request.basePackage,
            "dbName" to request.dbName,
            "database" to request.database,
			"enumsType" to request.enumsType,
			"defaultStringSize" to request.defaultStringSize.toInt(),
            "addKotlin" to request.addKotlin,
            "dateLibrary" to request.dateLibrary,
            "addBindingEntity" to request.addBindingEntity,
			"authorizationEnabled" to request.authorizationEnabled,
			"datasetStatus" to request.datasetStatus.name.toLowerCase()
        )
        YamlWriter(request.system).writeFile(configMap, configFolder, "settings")
    }

    private fun executeCodeGenerator(projectPath: String) {
		CodeGenerator().generate(projectPath)
    }
}
