package pro.bilous.difhub.load

import pro.bilous.difhub.auth.Authorizer

object TokenReader {

	private val authorizer = Authorizer()

	fun readAuth(username: String, password: String): String {
		return "Bearer ${authorizer.authToken(username, password)}"
	}
}
