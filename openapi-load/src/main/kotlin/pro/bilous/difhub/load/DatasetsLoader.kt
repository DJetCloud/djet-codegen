package pro.bilous.difhub.load

import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.config.SystemSettings
import pro.bilous.difhub.model.Model

class DatasetsLoader : IDatasetsLoader {
	override fun load(systemSettings: SystemSettings, app: String, type: String?): List<Model> {
		val difhub = ConfigReader.loadConfig().difhub
		val datasetList =
			ModelLoader(DefLoader()).loadModels(difhub.getDatasetsUrl(systemSettings.name, app))!!

		val datasets = mutableListOf<Model>()

		val allowedTypes =
			if (type.isNullOrEmpty()) {
				listOf("Structure", "Reference", "Resource", "Enum")
			} else {
				listOf(type)
			}

		datasetList
			.filter { it.`object` != null && allowedTypes.contains(it.`object`.usage) }
			.forEach {

				val url = difhub.getDatasetTypeUrl(systemSettings.name, app, it.identity.name)
//					if (it.version != null) {
//						url = "$url/versions/${it.version.major}.${it.version.minor}.${it.version.revision}"
//					}
				val dataset = ModelLoader(DefLoader()).loadModel(url, systemSettings)!!
				datasets.add(dataset)
			}
		return datasets
	}
}
