package pro.bilous.difhub.console

import pro.bilous.difhub.config.DatasetStatus
import pro.bilous.difhub.load.ApplicationsLoader
import pro.bilous.difhub.load.SystemsLoader
import java.lang.IllegalArgumentException

class Console {
	var system: String? = null
	var application: String? = null
	var status: DatasetStatus? = null

	fun select() {
		selectSystem()
		selectStatus()
	}

	private fun selectSystem() {
		val systems = SystemsLoader().loadSystems()
		system = selectFromList(systems, "System")
	}

	private fun selectApplication() {
		val applications = ApplicationsLoader().loadAppBySystem(system!!)
		application = selectFromList(applications, "Application")
	}

	private fun selectStatus() {
		val statuses = DatasetStatus.values().map { it.toString().toLowerCase() }
		try {
			status = DatasetStatus.valueOf(selectFromList(statuses, "Status").toUpperCase())
		} catch (e: IllegalArgumentException) {
			println("Selected status is invalid. The current status is set to `approved`")
		}
	}

	private fun selectFromList(names: List<String>, title: String): String {
		if (names.isEmpty()) {
			throw IllegalArgumentException("The list of items to be selected couldn't be empty")
		}
		val maxNumberOfAttempts = 5
		var counter = 0
		do {
			println("Choose number of the $title:")
			names.forEachIndexed { index, it -> println("$index. $it") }
			val select = readLine()?.trim()
			if (select.isNullOrEmpty()) {
				println("Please, enter the correct number to continue")
			} else {
				try {
					val number = select.toInt()
					if (number >= names.size || number < 0) {
						println("Number must be in range [0..${names.size - 1}]")
					} else {
						return names[number]
					}
				} catch (e: NumberFormatException) {
					println("Number is incorrect. Please, enter the correct number")
				}
			}
		} while (++counter < maxNumberOfAttempts)
		println("Any $title hasn't been selected")
		throw IllegalArgumentException("Any $title hasn't been selected")
	}
}
