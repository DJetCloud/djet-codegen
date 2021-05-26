package pro.bilous.difhub.load

import pro.bilous.difhub.config.SystemSettings
import pro.bilous.difhub.model.Model

interface IDatasetsLoader {
	fun load(systemSettings: SystemSettings, app: String, type: String? = null): List<Model>?
}
