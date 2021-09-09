package pro.bilous.codegen.process.deployment

class DeploymentFileResolver {

	data class ResultArgs(
		val filePath: String,
		val templateData: Map<String, Any>
	)

	fun resolveDeployment(templateData: Map<String, Any>, templateName: String, filePath: String): List<ResultArgs> {
		val deployment = templateData["deployment"] as List<Map<String, String>>?
			?: return listOf(ResultArgs(filePath.replace("\$env/", "env/"), templateData))

		val deploymentResult = mutableListOf<ResultArgs>()

		deployment.forEach {
			val deploymentName = it["name"] as String
			val dataCopy = templateData.toMutableMap()
			dataCopy["deployment"] = it
			deploymentResult.add(
				ResultArgs(
					filePath.replace("\$env/", "$deploymentName/"),
					dataCopy
				)
			)
		}
		return deploymentResult
	}
}
