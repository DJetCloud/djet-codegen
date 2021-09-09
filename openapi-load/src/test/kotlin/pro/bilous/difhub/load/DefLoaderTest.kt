package pro.bilous.difhub.load

import okhttp3.Request
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import java.net.SocketTimeoutException
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefLoaderTest {

	companion object {
		const val USERNAME = "user"
		const val PASSWORD = "pass"
		const val RESULT = "result"
		const val URL = "http:\\test"
		const val TOKEN = "token"
	}

	@Test
	fun `should load if user is authorized and path is valid`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = Pair(200, RESULT)
		}
		assertEquals(RESULT, defLoader.load(""))
	}

	@Test
	fun `should not load if timeout occurs`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = throw SocketTimeoutException("timeout")
		}
		assertNull(defLoader.load(""))
	}

	@Test
	fun `should not load if user is not authorized`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = throw Exception("user is not authorized")
			override fun call(request: Request) = Pair(200, RESULT)
		}
		assertThrows<Exception>("user is not authorized") { defLoader.load("") }
	}

	@Test
	fun `should not load if content is not found (first case)`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = Pair(404, "")
		}
		assertNull(defLoader.load(""))
	}

	@Test
	fun `should not load if content is not found (second case)`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = Pair(200, "\"status\": 404")
		}
		assertNull(defLoader.load(""))
	}

	@Test
	fun `should return null if access is forbidden (first case)`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = Pair(403, RESULT)
		}
		assertNull(defLoader.load(""))
	}

	@Test
	fun `should return null if access is forbidden (second case)`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = Pair(200, "\"status\": 403")
		}
		assertNull(defLoader.load(""))
	}

	@Test
	fun `should not load if content is null`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = Pair(200, null)
		}
		assertNull(defLoader.load(""))
	}

	@Test
	fun `should load after repeated authorization`() {
		DefLoader.dropAuthTokens()
		val defLoader = object : DefLoader(USERNAME, PASSWORD) {
			var authCount = 0
			override fun getUrl(path: String) = URL
			override fun getAuthToken() = TOKEN
			override fun call(request: Request) = when (++authCount) {
				1 -> Pair(401, "")
				2 -> Pair(200, "\"status\": 401")
				else -> Pair(200, RESULT)
			}
		}
		assertEquals(RESULT, defLoader.load(""))
	}
}
