package cloud.djet.config

data class DjetConfig(
	val organization: String? = null,
	val system: String? = null,
	val datasetStatus: String? = null,
	val migrations: Boolean = false
)
