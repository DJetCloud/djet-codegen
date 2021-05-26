package pro.bilous.difhub.load

import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.config.SystemSettings
import pro.bilous.difhub.model.Model

class ApplicationsLoader {

	var modelLoader: IModelLoader = ModelLoader(DefLoader())
	var config = ConfigReader.loadConfig()

	fun loadAppBySystem(system: String): List<String> {
		val difhub = config.difhub
		val models = modelLoader.loadModels(difhub.getApplicationsUrl(system))

		val apps = mutableListOf<String>()
		models?.filter {
			it.`object`!!.usage == "Service"
		}?.forEach {
			val name = it.identity.name
			if (name.isNotEmpty()) {
				apps.add(name)
			}
		}
		return apps
	}

	fun loadAll(system: String): List<Model>? {
		val difhub = config.difhub
		return modelLoader.loadModels(difhub.getApplicationsUrl(system))
	}

	fun loadOne(systemSettings: SystemSettings, app: String): Model? {
		val difhub = config.difhub
		return modelLoader.loadModel(difhub.getApplicationUrl(systemSettings.name, app), systemSettings)
	}

	fun loadAppSettings(systemSettings: SystemSettings, app: String): Model? {
		val difhub = config.difhub
		return modelLoader.loadModel(difhub.getApplicationSettingsUrl(systemSettings.name, app), systemSettings)
	}

}
