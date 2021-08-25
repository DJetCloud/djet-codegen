package cloud.djet.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ConfigReaderTest {

	@Test
	fun `should read config from file`() {
		val fileText =
			"""
				organization: Test Org
				system: This System
				datasetStatus: APPROVED
				migrations: true
			""".trimIndent()
		val config = ConfigReader.readFrom(fileText.byteInputStream())!!

		assertEquals("Test Org", config.organization)
		assertEquals("This System", config.system)
		assertEquals("APPROVED", config.datasetStatus)
		assertEquals(true, config.migrations)
	}
}
