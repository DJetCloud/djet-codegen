package pro.bilous.difhub.load

import pro.bilous.difhub.config.SystemSettings
import pro.bilous.difhub.model.Model

interface IModelLoader {
    fun loadModel(reference: String, systemSettings: SystemSettings): Model?
    fun loadModels(reference: String): List<Model>?
}
