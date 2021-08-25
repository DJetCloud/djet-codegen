package cloud.djet.config

import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.util.Yaml
import java.io.InputStream

object ConfigReader {
	fun readFrom(inputStream: InputStream): DjetConfig? {
		val configTree = Yaml.mapper().readTree(inputStream) as? ObjectNode ?: return null
		return DjetConfig(
			organization = configTree.get("organization").asText(),
			system = configTree.get("system").asText(),
			datasetStatus = configTree.get("datasetStatus")?.asText() ?: "DRAFT",
			migrations = configTree.get("migrations")?.asBoolean() ?: false
		)
	}
}
