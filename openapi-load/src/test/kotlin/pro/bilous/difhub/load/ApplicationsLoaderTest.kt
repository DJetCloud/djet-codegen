package pro.bilous.difhub.load

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test
import pro.bilous.difhub.config.Config
import pro.bilous.difhub.config.DatasetStatus
import pro.bilous.difhub.config.DifHub
import pro.bilous.difhub.model.Identity
import pro.bilous.difhub.model.Model
import pro.bilous.difhub.model.Object
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationsLoaderTest {

	@Test
	fun `test applications load`() {
		val config = Config(DifHub("test", "test"))
		config.system = "system"

		val modelLoaderMock = mock<IModelLoader> {
			on { loadModels(config.difhub.getApplicationsUrl("system")) } doReturn
					listOf(
						Model(
							identity = Identity(name = "application1"),
							`object` = Object(usage = "Service")
						),
						Model(
							identity = Identity(name = "application2"),
							`object` = Object(usage = "Service")
						),					)
		}
		val loader = ApplicationsLoader(modelLoaderMock, config)
		val apps = loader.loadAppBySystem("system")

		assertTrue { apps.isNotEmpty() }
		assertEquals("application1", apps[0])
		assertEquals("application2", apps[1])
	}

	@Test
	fun `test applications load Service only`() {
		val config = Config(DifHub("test", "test"))

		val modelLoaderMock = mock<IModelLoader> {
			on { loadModels(config.difhub.getApplicationsUrl("system")) } doReturn
					listOf(
						Model(
							identity = Identity(name = "application1"),
							`object` = Object(usage = "Schema")
						),
						Model(
							identity = Identity(name = "application2"),
							`object` = Object(usage = "Service")
						),					)
		}
		val loader = ApplicationsLoader(modelLoaderMock, config)
		val apps = loader.loadAppBySystem("system")

		assertTrue { apps.isNotEmpty() }
		assertEquals("application2", apps[0])
	}

	@Test
	fun `load all application models`() {
		val config = Config(DifHub("test", "test"))
		config.system = "system"

		val modelLoaderMock = mock<IModelLoader> {
			on { loadModels(config.difhub.getApplicationsUrl("system")) } doReturn
					listOf(
						Model(identity = Identity(name = "application1")),
						Model(identity = Identity(name = "application2")),					)
		}
		val loader = ApplicationsLoader(modelLoaderMock, config)
		val apps = loader.loadAll() ?: throw IllegalArgumentException("should not return null")

		assertTrue { apps.isNotEmpty() }
		assertEquals("application1", apps[0].identity.name)
		assertEquals("application2", apps[1].identity.name)
	}

	@Test
	fun `load one application model`() {
		val config = Config(DifHub("test", "test"))
		config.system = "system"
		config.datasetStatus = DatasetStatus.APPROVED

		val modelLoaderMock = mock<IModelLoader> {
			on { loadModel(config.difhub.getApplicationUrl("system", "app"), DatasetStatus.APPROVED) } doReturn
						Model(identity = Identity(name = "application1"))
		}
		val loader = ApplicationsLoader(modelLoaderMock, config)
		val app = loader.loadOne("app") ?: throw IllegalArgumentException("should not return null")

		assertNotNull(app)
		assertEquals("application1",app.identity.name)
	}

	@Test
	fun `load one application settings`() {
		val config = Config(DifHub("test", "test"))
		config.system = "system"
		config.datasetStatus = DatasetStatus.APPROVED

		val modelLoaderMock = mock<IModelLoader> {
			on { loadModel(config.difhub.getApplicationSettingsUrl("system", "app"), DatasetStatus.APPROVED) } doReturn
					Model(identity = Identity(name = "settings1"))
		}
		val loader = ApplicationsLoader(modelLoaderMock, config)
		val settings = loader.loadAppSettings("app") ?: throw IllegalArgumentException("should not return null")

		assertNotNull(settings)
		assertEquals("settings1", settings.identity.name)
	}

}

