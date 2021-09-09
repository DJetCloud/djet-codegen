package pro.bilous.difhub

import pro.bilous.difhub.console.Console
import pro.bilous.difhub.convert.DifHubToSwaggerConverter
import pro.bilous.difhub.write.YamlWriter

fun main() {
	val console = Console()
	console.select()
	DifHubToSwaggerConverter(console.modelLoader, console.config).convertAll().forEach {
		YamlWriter(it.appName).write(it.openApi)
	}
}


