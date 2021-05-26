package pro.bilous.difhub.load

import pro.bilous.difhub.config.DatasetStatus
import pro.bilous.difhub.config.SystemSettings

class InterfacesLoaderTest {
}

fun main() {
	InterfacesLoader().load(SystemSettings("Customer", DatasetStatus.APPROVED), "Product")
}
