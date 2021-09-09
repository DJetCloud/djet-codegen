package pro.bilous.intellij.plugin

import com.intellij.openapi.util.io.FileSystemUtil
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PathToolsTest {

	companion object {
		const val OLD_HOME_PATH = ".difhub-codegen"
		const val HOME_PATH = "djet"
	}

    @Test
    fun `should return HomePath of project`() {
		val projectPath = Paths.get("build/tmp").toAbsolutePath().toString()
		val homePath = "$projectPath/$HOME_PATH"
		assertEquals(homePath, PathTools.getHomePath(projectPath))
    }

    @Test
    fun `should rename old home folder and return HomePath of project`() {
		val projectPath = Paths.get("build/tmp").toAbsolutePath().toString()

		val homePath = "$projectPath/$HOME_PATH"
		val homeFolder = File(homePath)
		deleteFolder(homeFolder)

		val oldHomePath = "$projectPath/$OLD_HOME_PATH"
		val oldHomeFolder = File(oldHomePath)
		if(!oldHomeFolder.exists()) {
			oldHomeFolder.mkdir()
		}

		assertEquals(homePath, PathTools.getHomePath(projectPath))
		assertTrue(homeFolder.exists())
    }

	private fun deleteFolder(folder: File) {
		if (!folder.exists()) return
		folder.listFiles()?.forEach {
			deleteFolder(it)
		}
		folder.delete()
	}
}
