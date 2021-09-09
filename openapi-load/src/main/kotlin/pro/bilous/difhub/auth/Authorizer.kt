package pro.bilous.difhub.auth

class Authorizer {

	private val cognito = CognitoWrapper()

	fun authToken(username: String, password: String): String {
		return cognito.login(username, password)
	}
}
