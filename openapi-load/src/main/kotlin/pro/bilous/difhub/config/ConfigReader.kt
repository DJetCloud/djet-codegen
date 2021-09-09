package pro.bilous.difhub.config
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

object ConfigReader {

	private const val DEFAULT_ORGANIZATION = "Demo org"
	private const val DEFAULT_API_URL = "https://metaserviceprod.azurewebsites.net/api"

	private val mapper = createMapper()

	private fun createMapper(): ObjectMapper {
		return ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
	}

	fun loadConfig(organization: String? = null): Config {
		val resolvedOrg = organization ?: let {
			var orgFromSystem = System.getProperty("DIFHUB_ORG_NAME")
			if (orgFromSystem.isNullOrEmpty()) {
				orgFromSystem = System.getenv("DIFHUB_ORG_NAME") ?: DEFAULT_ORGANIZATION
			}
			orgFromSystem
		}
		return Config(DifHub(DEFAULT_API_URL, "/$resolvedOrg"))
//		val configUrl = ConfigReader::class.java.getResource("/config.yaml")
//		val path = Paths.get(configUrl.toURI())
//		return loadFromFile(path)
	}

//	private fun loadFromFile(path: Path): Config {
//		return Files.newBufferedReader(path).use {
//			mapper.readValue(it, Config::class.java)
//		}
//	}
}
