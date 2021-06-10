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
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

group = "cloud.djet"
version = "1.0.0-alpha"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2021.1.1"
}

tasks.patchPluginXml {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
    sinceBuild("192")
}
