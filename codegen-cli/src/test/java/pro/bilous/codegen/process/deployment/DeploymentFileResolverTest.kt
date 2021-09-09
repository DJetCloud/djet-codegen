package pro.bilous.codegen.process.deployment

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeploymentFileResolverTest {

	@Test
	fun `should remove $env from file name when missing deployment`() {
		val templateData = mutableMapOf<String, Any>()
		val templateName = "kube/\$env/configmap.yml.mustache"
		val filePath = "/kube/\$env/configmap.yml"

		val result = DeploymentFileResolver().resolveDeployment(templateData, templateName, filePath)

		assertEquals("/kube/env/configmap.yml", result.first().filePath)
	}

	@Test
	fun `should point path and data to integration env`() {
		val inputTemplateData = mutableMapOf<String, Any>(
			"deployment" to mutableListOf<Map<String, String>>(mutableMapOf(
				"name" to "integration",
				"dbHost" to "some-db-host.net"
			))
		)
		val templateName = "kube/\$env/configmap.yml.mustache"
		val filePath = "/kube/\$env/configmap.yml"

		val result = DeploymentFileResolver().resolveDeployment(inputTemplateData, templateName, filePath).first()

		assertEquals("/kube/integration/configmap.yml", result.filePath)

		val deployment = result.templateData["deployment"]
		assertTrue(deployment is Map<*, *>)
		deployment as Map<String, String>
		assertEquals("some-db-host.net", deployment["dbHost"])
		assertEquals("integration", deployment["name"])
	}
}
