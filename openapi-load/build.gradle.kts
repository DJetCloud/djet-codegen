
configure<SourceSetContainer> {
	named("main") {
		java.srcDir("src/main/kotlin")
	}
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	api("io.swagger.parser.v3:swagger-parser:2.0.26")
	implementation("org.kohsuke.metainf-services:metainf-services:1.8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
	implementation("com.squareup.okhttp3:okhttp:4.9.1")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
	implementation("com.amazonaws:aws-java-sdk-cognitoidp:1.12.4")
	implementation("org.json:json:20210307")

	testImplementation(kotlin("test-junit"))
	testImplementation("org.mockito:mockito-core:3.11.0")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
