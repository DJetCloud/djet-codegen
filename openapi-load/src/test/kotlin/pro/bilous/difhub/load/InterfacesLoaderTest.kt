package pro.bilous.difhub.load

import com.nhaarman.mockitokotlin2.mock
import pro.bilous.difhub.config.ConfigReader

class InterfacesLoaderTest {
}

fun main() {
	val mockModelLoader: IModelLoader = mock()
	val config = ConfigReader.loadConfig()
	InterfacesLoader(mockModelLoader, config).load("Product")
}
