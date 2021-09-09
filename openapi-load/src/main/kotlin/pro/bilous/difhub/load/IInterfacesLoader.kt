package pro.bilous.difhub.load

import pro.bilous.difhub.model.Model

interface IInterfacesLoader {
	fun load(app: String): List<Model>
}
