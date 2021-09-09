package pro.bilous.intellij.plugin.action.menu

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import pro.bilous.intellij.plugin.Migrations
import java.lang.IllegalArgumentException

class VerifiedEvent(val e: AnActionEvent) {
	val project: Project = e.project ?: throw IllegalArgumentException("Project not found")
	val projectPath: String = project.basePath ?: throw IllegalArgumentException("Base path not found")

	init {
		Migrations.movePropertyOrganizationFromCredentialsToSettings(projectPath)
	}
}
