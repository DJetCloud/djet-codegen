package pro.bilous.difhub.load

import pro.bilous.difhub.config.DatasetStatus
import pro.bilous.difhub.model.Model

interface IModelLoader {
    fun loadModel(reference: String, datasetStatus: DatasetStatus): Model?
    fun loadModels(reference: String): List<Model>?
}
