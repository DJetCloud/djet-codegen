package pro.bilous.intellij.plugin.project

import pro.bilous.intellij.plugin.Icons
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import pro.bilous.difhub.config.Config
import pro.bilous.difhub.load.IModelLoader

class ProjectModuleBuilder: ModuleBuilder() {

	var modelLoader: IModelLoader? = null
	var config: Config? = null
    var request = ProjectCreationRequest()
    val projectFilesCreator = ProjectFilesCreator()

    override fun getModuleType() = StdModuleTypes.JAVA
    override fun getNodeIcon() = Icons.SpringBoot
    override fun getBuilderId() = "Create System"
    override fun getDescription() = "Bootstrap system using MetUCat metadata"
    override fun getPresentableName() = "DJet Bootstrap"
    override fun getParentGroup() = "Build Tools"

    override fun createWizardSteps(wizardContext: WizardContext,
                                   modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
        return arrayOf(ProjectDetailsStep(this, wizardContext))
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        doAddContentEntry(modifiableRootModel)
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep {
        return ServerSelectionStep(this)
    }

    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? {
        val moduleSettings = settingsStep.moduleNameLocationSettings
        if (moduleSettings != null) {
            moduleSettings.moduleName = request.artifactId
        }
        return super.modifySettingsStep(settingsStep)
    }

    override fun createModule(moduleModel: ModifiableModuleModel): Module {
		val loader = modelLoader ?: throw IllegalArgumentException("Module can't be created - model loader is absent")
		val cfg = config ?: throw IllegalArgumentException("Module can't be created - config is absent")
		cfg.apply {
			system = request.system
			datasetStatus = request.datasetStatus
		}
        val module = super.createModule(moduleModel)
        projectFilesCreator.createFiles(loader, cfg, module, request)
        return module
    }
}
