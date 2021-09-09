package pro.bilous.difhub.load

import pro.bilous.difhub.config.Config
import pro.bilous.difhub.model.Model

class ApplicationsLoader(val modelLoader: IModelLoader, val config: Config) {

	private val difhub = config.difhub
	private val system = config.system
	private val datasetStatus = config.datasetStatus

	fun loadAppBySystem(systemToLoad: String): List<String> {
		val models = modelLoader.loadModels(difhub.getApplicationsUrl(systemToLoad))

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

	fun loadAll(): List<Model>? {
		return modelLoader.loadModels(difhub.getApplicationsUrl(system))
	}

	fun loadOne(app: String): Model? {
		return modelLoader.loadModel(difhub.getApplicationUrl(system, app), datasetStatus)
	}

	fun loadAppSettings(app: String): Model? {
		return modelLoader.loadModel(difhub.getApplicationSettingsUrl(system, app), datasetStatus)
	}

}
