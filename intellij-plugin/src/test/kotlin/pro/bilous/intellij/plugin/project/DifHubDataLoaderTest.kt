package pro.bilous.intellij.plugin.project

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

import org.junit.Assert.*
import pro.bilous.difhub.config.Config
import pro.bilous.difhub.config.DifHub
import pro.bilous.difhub.load.IModelLoader
import pro.bilous.difhub.model.Identity
import pro.bilous.difhub.model.Model
import pro.bilous.difhub.model.Object

class DifHubDataLoaderTest {

	companion object {
		const val SYSTEM_1 = "system1"
		const val SYSTEM_2 = "system2"
		const val APP_1_1 = "app1.1"
		const val APP_1_2 = "app1.2"
		const val APP_1_3 = "app1.3"
		const val APP_2_1 = "app2.1"
		const val APP_2_2 = "app2.2"
	}

    @Test
    fun loadAllSystemsAndApps() {
		val difhub = DifHub("api", "org")
		val systemsUrl = difhub.getSystemsUrl()
		val appsUrlOfSystem1 = difhub.getApplicationsUrl(SYSTEM_1)
		val appsUrlOfSystem2 = difhub.getApplicationsUrl(SYSTEM_2)

		val config = Config(difhub)

		val modelLoaderMock = mock<IModelLoader> {
			on { loadModels(systemsUrl) } doReturn listOf(sysModel(SYSTEM_1), sysModel(SYSTEM_2))
			on { loadModels(appsUrlOfSystem1) } doReturn listOf(appModel(APP_1_1), appModel(APP_1_2), appModel(APP_1_3))
			on { loadModels(appsUrlOfSystem2) } doReturn listOf(appModel(APP_2_1), appModel(APP_2_2))
		}

		val difHubDataLoader = DifHubDataLoader()

		assertEquals(
			mapOf(SYSTEM_1 to listOf(APP_1_1, APP_1_2, APP_1_3), SYSTEM_2 to listOf(APP_2_1, APP_2_2)),
			difHubDataLoader.loadAllSystemsAndApps(modelLoaderMock, config)
		)
    }

	private fun sysModel(system: String) : Model {
		return Model(identity = Identity(name = system))
	}

	private fun appModel(app: String) : Model {
		return Model(
			identity = Identity(name = app),
			`object` = Object(usage = "Service")
		)
	}
}
