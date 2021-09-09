package pro.bilous.codegen.process.deployment

import org.openapitools.codegen.CodeCodegen

class DeploymentPostProcessor(val codegen: CodeCodegen) {

	private val additionalProperties = codegen.additionalProperties()

	fun addSystemFiles() {
		codegen.addSupportFile(
			source = ".gitlab-ci.yml.mustache",
			target = ".gitlab-ci.yml",
			condition = cicdEnabled()
		)
		// add kubernetes ConfigMap manifest to the application
		codegen.addSupportFile(
			source = "kube/\$env/configmap.yml.mustache",
			target = "kube/\$env/configmap.yml",
			condition = cicdEnabled()
		)
		codegen.addSupportFile(
			source = "kube/deploy.md.mustache",
			target = "kube/deploy.md",
			condition = cicdEnabled()
		)
	}

	fun addAppFiles(artifactId: String) {
		codegen.addSupportFile(
			source = "kube/kube-app.yml.mustache",
			target = "kube/kube-${artifactId.toLowerCase()}.yml",
			condition = cicdEnabled()
		)
	}

	private fun cicdEnabled() = additionalProperties.containsKey("cicd") && additionalProperties["cicd"] as Boolean
}
