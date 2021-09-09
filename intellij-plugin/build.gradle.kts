plugins {
    id("org.jetbrains.intellij")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":openapi-load"))
    implementation(project(":codegen-cli"))

	testImplementation(kotlin("test-junit"))
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core:3.5.11")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}

group = "cloud.djet"
version = "1.0.0-alpha"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
	version.value("2021.1.2")
}

//tasks.patchPluginXml {
//    changeNotes("""
//      Add change notes here.<br>
//      <em>most HTML tags may be used</em>""")
//    sinceBuild("192")
//}
