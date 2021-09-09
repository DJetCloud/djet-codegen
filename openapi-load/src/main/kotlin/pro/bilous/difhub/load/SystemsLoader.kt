package pro.bilous.difhub.load

import pro.bilous.difhub.config.Config

class SystemsLoader(private val modelLoader: IModelLoader, config: Config) {

	var difhub = config.difhub

	fun loadSystems(): List<String> {

		val models = modelLoader.loadModels(difhub.getSystemsUrl())
		val systems = mutableListOf<String>()

		models?.forEach {
			val name = it.identity.name
			if (name.isNotEmpty()) {
				systems.add(name)
			}
		}

		return systems
	}
}
