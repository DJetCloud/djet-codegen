package pro.bilous.difhub.load

import pro.bilous.difhub.config.SystemSettings
import pro.bilous.difhub.model.Model

interface IInterfacesLoader {
	fun load(systemSettings: SystemSettings, app: String): List<Model>?
}
