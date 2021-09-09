package pro.bilous.difhub.load

import pro.bilous.difhub.config.Config
import pro.bilous.difhub.model.Model

class DatasetsLoader(val modelLoader: IModelLoader, val config: Config) : IDatasetsLoader {

	private val difhub = config.difhub
	private val system = config.system
	private val datasetStatus = config.datasetStatus

	override fun load(app: String, type: String?): List<Model> {
		val datasetList = modelLoader.loadModels(difhub.getDatasetsUrl(system, app))!!

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

				val url = difhub.getDatasetTypeUrl(system, app, it.identity.name)
//					if (it.version != null) {
//						url = "$url/versions/${it.version.major}.${it.version.minor}.${it.version.revision}"
//					}
				val dataset = modelLoader.loadModel(url, datasetStatus)!!
				datasets.add(dataset)
			}
		return datasets
	}
}
