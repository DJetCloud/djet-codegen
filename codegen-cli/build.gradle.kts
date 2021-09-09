
sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("com.google.guava:guava:30.1.1-jre")
    api("io.airlift:airline:0.9")
    implementation("com.googlecode.lambdaj:lambdaj:2.3.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.openapitools:openapi-generator:5.1.1")
    implementation("junit:junit:4.13.2")

	testImplementation(kotlin("test-junit"))
	testImplementation("org.mockito:mockito-core:3.11.0")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
