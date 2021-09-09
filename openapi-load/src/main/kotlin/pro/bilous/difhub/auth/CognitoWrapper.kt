package pro.bilous.difhub.auth

import pro.bilous.difhub.j.AuthenticationHelper

class CognitoWrapper {
	companion object {
		const val USER_POOL = "us-east-2_DVwWFfSPl"
		const val CLIENT_ID = "5tot1acvq11qksavvhlh0qr3fi"
	}

	fun login(username: String, password: String): String {
		val helper = AuthenticationHelper(USER_POOL, CLIENT_ID, "")
		return helper.PerformSRPAuthentication(username, password)
			?: throw Exception("Authentication failed. Please check username and password")
	}
}
