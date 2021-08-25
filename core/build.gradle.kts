plugins {
	kotlin("jvm")
}

dependencies {
	api("io.swagger.parser.v3:swagger-parser:2.0.26")

	testImplementation(kotlin("test-junit"))
	testImplementation("org.mockito:mockito-core:3.11.0")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
