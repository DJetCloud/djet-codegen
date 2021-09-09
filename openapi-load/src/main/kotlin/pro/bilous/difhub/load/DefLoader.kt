package pro.bilous.difhub.load

import okhttp3.OkHttpClient
import okhttp3.Request
import pro.bilous.difhub.config.Config
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

abstract class DefLoader(protected val username: String, protected val password: String) {
	companion object {
		private val authTokens = ConcurrentHashMap<String, String>()
		private const val MAX_LOAD_ATTEMPTS = 5

		fun dropAuthTokens() {
			authTokens.clear()
		}
	}

	fun load(path: String): String? {
		val url = getUrl(path)
		val (code, result) = loadFromUrl(url) ?: return null
		if (result != null) {
			if (isUnauthorized(code, result)) {
				println("User is unauthorized. Trying to authorize and load again url: $url")
				authTokens[username] = getAuthToken()
				//File("/difhub.auth").writeText(authHeader) //TODO  Exception in thread "main" java.lang.IllegalArgumentException: URI is not hierarchical
				return load(path)
			} else if (isNotFound(code, result)) {
				println("Url is not found: $url")
				return null
			} else if (isForbidden(code, result)) {
				println("User is authorized but access is forbidden for: $url")
				return null
			}
			println("Url is loaded: $url")
		} else {
			println("Url is not loaded: $url")
		}
		return result
	}

	private fun loadFromUrl(url: String): Pair<Int, String?>? {
		val request = Request.Builder()
			.url(url)
			.addHeader("Authorization", authTokens.getOrPut(username) { getAuthToken() })
			.build()

		var loadAttempts = 0
		while (loadAttempts++ < MAX_LOAD_ATTEMPTS) {
			try {
				return call(request)
			} catch (e: SocketTimeoutException) {
				Thread.sleep(3000)
				println("Failed $loadAttempts attempt to load: retry in 3 seconds")
			}
		}
		println("Max retry attempt to load is reached. Url is not loaded: $url")
		return null
	}

	private fun isUnauthorized(code: Int, body: String): Boolean {
		return code == 401 || body.contains("\"status\": 401")
	}

	private fun isNotFound(code: Int, body: String): Boolean {
		return code == 404 || body.contains("\"status\": 404")
	}

	private fun isForbidden(code: Int, body: String): Boolean {
		return code == 403 || body.contains("\"status\": 403")
	}

	protected abstract fun getUrl(path: String): String

	protected abstract fun getAuthToken(): String

	protected abstract fun call(request: Request): Pair<Int, String?>
}

class DifHubLoader(username: String, password: String, val config: Config) : DefLoader(username, password) {
	companion object {
		private val client = OkHttpClient.Builder()
			.retryOnConnectionFailure(true)
			.readTimeout(7, TimeUnit.SECONDS)
			.build()
	}

	val api = config.difhub.api

	override fun getUrl(path: String) = "$api/$path"

	override fun getAuthToken() = TokenReader.readAuth(username, password)

	override fun call(request: Request): Pair<Int, String?> {
		val response = client.newCall(request).execute()
		return Pair(response.code, response.body?.string())
	}

}
