package pro.bilous.difhub.load

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import io.swagger.util.Json
import okhttp3.Request
import pro.bilous.difhub.config.DatasetStatus
import org.junit.jupiter.api.Assertions.*
import pro.bilous.difhub.model.Model

class ModelLoaderTest {

	companion object {
		val LOADED_MODEL = """
			{
  				"identity": {
    				"id": "f94b7b29-5999-4ee7-b77f-0a5ab2e305e0",
    				"name": "Organization",
    				"description": "Test model"
  				},
  				"object": {
    				"parent": {
      					"id": "9bd2d779-cb46-4ce4-ab67-fc3417c720a4",
      					"name": "/organizations/Test/systems/Healthcare"
    				},
    				"tags": [],
    				"documents": [],
    				"type": "Application",
    				"usage": "Service",
    				"access": "External",
    				"properties": [],
    				"history": {
      					"created": "2020-11-30T12:35:17.99",
      					"createdBy": "sashaberger@hotmail.com",
      					"updated": "2020-11-30T12:35:17.99",
      					"updatedBy": "v.bilous@spd-ukraine.com",
      					"completions": []
    				},
    				"alias": "OrgService"
  				}
			}
			""".trimIndent()
	}

	val jsonMapper = Json.mapper()

	init {
		jsonMapper.registerKotlinModule()
	}

	@Test
	fun `should load model if reference valid and use cache`() {
		ModelLoader.clearCache()
		DefLoader.dropAuthTokens()

		val defLoader = object : DefLoader("user", "pass") {
			override fun getUrl(path: String) = "http://test/$path"
			override fun getAuthToken() = "token"
			override fun call(request: Request) = Pair(200, LOADED_MODEL)
		}

		val modelLoader = ModelLoader(defLoader)
		val reference = "//model////versions///v1.2.0"

		val result = modelLoader.loadModel(reference, DatasetStatus.APPROVED)
		val test = jsonToModel(LOADED_MODEL)
		assertEquals(test, result)
	}

	@Test
	fun `should use cache if model has been loaded earlier`() {
		ModelLoader.clearCache()
		DefLoader.dropAuthTokens()

		val defLoader = object : DefLoader("user", "pass") {
			override fun getUrl(path: String) = "http://test/$path"
			override fun getAuthToken() = "token"
			override fun call(request: Request) = Pair(200, LOADED_MODEL)
		}

		val modelLoader = ModelLoader(defLoader)
		val reference = "//model?parameter=42"

		modelLoader.loadModel(reference, DatasetStatus.APPROVED)
		val fromCache = modelLoader.loadModel(reference, DatasetStatus.APPROVED)
		val test = jsonToModel(LOADED_MODEL)
		assertEquals(test, fromCache)
	}

	@Test
	fun `should return null if model is not loaded`() {
		ModelLoader.clearCache()
		DefLoader.dropAuthTokens()

		val defLoader = object : DefLoader("user", "pass") {
			override fun getUrl(path: String) = "http://test/$path"
			override fun getAuthToken() = "token"
			override fun call(request: Request) = Pair(404, null)
		}

		val modelLoader = ModelLoader(defLoader)
		val reference = "/model/versions/v1.2.0"

		val result = modelLoader.loadModel(reference, DatasetStatus.DRAFT)
		assertNull(result)
	}

	@Test
	fun `should return null if request is null`() {
		ModelLoader.clearCache()
		DefLoader.dropAuthTokens()

		val defLoader = object : DefLoader("user", "pass") {
			override fun getUrl(path: String) = "http://test/$path"
			override fun getAuthToken() = "token"
			override fun call(request: Request) = Pair(200, null)
		}

		val modelLoader = ModelLoader(defLoader)
		val reference = "/model/versions/v1.2.0"

		val result = modelLoader.loadModel(reference, DatasetStatus.DRAFT)
		assertNull(result)
	}

	@Test
	fun `should return null if request is empty`() {
		ModelLoader.clearCache()
		DefLoader.dropAuthTokens()

		val defLoader = object : DefLoader("user", "pass") {
			override fun getUrl(path: String) = "http://test/$path"
			override fun getAuthToken() = "token"
			override fun call(request: Request) = Pair(200, "")
		}

		val modelLoader = ModelLoader(defLoader)
		val reference = "/model/versions/v1.2.0"

		val result = modelLoader.loadModel(reference, DatasetStatus.DRAFT)
		assertNull(result)
	}

	@Test
	fun `should return null if request is not JSON`() {
		ModelLoader.clearCache()
		DefLoader.dropAuthTokens()

		val defLoader = object : DefLoader("user", "pass") {
			override fun getUrl(path: String) = "http://test/$path"
			override fun getAuthToken() = "token"
			override fun call(request: Request) = Pair(200, "not JSON")
		}

		val modelLoader = ModelLoader(defLoader)
		val reference = "/model/versions/v1.2.0"

		val result = modelLoader.loadModel(reference, DatasetStatus.DRAFT)
		assertNull(result)
	}

	@Test
	fun loadModels() {
	}

	private fun jsonToModel(jsonText: String) =	jsonMapper.readValue(jsonText, Model::class.java)
}
