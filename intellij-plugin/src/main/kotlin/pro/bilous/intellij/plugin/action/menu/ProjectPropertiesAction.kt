package pro.bilous.intellij.plugin.action.menu

import pro.bilous.intellij.plugin.PathTools
import pro.bilous.intellij.plugin.project.ProjectFileManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.VirtualFileManager

class ProjectPropertiesAction : AnAction() {

	private val fileManager = ProjectFileManager()

    override fun actionPerformed(e: AnActionEvent) {
		val ve = VerifiedEvent(e)
		val project = ve.project
		val projectPath = ve.projectPath

        val filePath = PathTools.getSettingsPath(projectPath)
        val file = VirtualFileManager.getInstance().findFileByUrl("file://$filePath")
		if (file == null) {
			fileManager.createAndOpenProjectSettings(PathTools.getHomePath(projectPath), project)
		} else {
			OpenFileDescriptor(project, file).navigate(true)
		}
    }
}
