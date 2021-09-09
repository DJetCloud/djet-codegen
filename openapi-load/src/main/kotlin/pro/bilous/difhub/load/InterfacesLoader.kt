package pro.bilous.difhub.load

import pro.bilous.difhub.config.Config
import pro.bilous.difhub.model.Model

class InterfacesLoader(val modelLoader: IModelLoader, val config: Config) : IInterfacesLoader {

	private val difhub = config.difhub
	private val system = config.system
	private val datasetStatus = config.datasetStatus

	override fun load(app: String): List<Model> {
		val interfaceList =  modelLoader.loadModels(difhub.getInterfacesUrl(system, app))

		val interfaces = mutableListOf<Model>()

		interfaceList?.forEach {
			val `interface` = modelLoader.loadModel(difhub.getInterfaceUrl(system, app, it.identity.name), datasetStatus)
			if (`interface` != null) {
				interfaces.add(`interface`)
			} else {
				println("Interface is not loaded: $it")
			}
		}
		return interfaces
	}
}
