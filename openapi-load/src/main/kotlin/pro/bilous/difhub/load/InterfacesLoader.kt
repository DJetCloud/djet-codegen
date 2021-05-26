package pro.bilous.difhub.load

import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.config.SystemSettings
import pro.bilous.difhub.model.Model

class InterfacesLoader : IInterfacesLoader {
	override fun load(systemSettings: SystemSettings, app: String): List<Model>? {
		val difhub = ConfigReader.loadConfig().difhub

		val interfaceList =  ModelLoader(DefLoader()).loadModels(difhub.getInterfacesUrl(systemSettings.name, app))

		val intefaces = mutableListOf<Model>()

		interfaceList?.forEach {
			val `interface` = ModelLoader(DefLoader()).loadModel(difhub.getInterfaceUrl(systemSettings.name, app, it.identity.name), systemSettings)!!
			intefaces.add(`interface`)
		}
		return intefaces
	}
}
