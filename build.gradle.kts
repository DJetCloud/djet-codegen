plugins {
	kotlin("jvm") version "1.5.10"
	id("org.jetbrains.intellij") version "1.0" apply false
	id("org.jetbrains.gradle.plugin.idea-ext") version "1.0" apply false
	jacoco
}

group = "cloud.djet"
version = "1.0.0"

allprojects {
	repositories {
		mavenLocal()
		mavenCentral()
	}
	tasks.withType<JavaCompile> {
		sourceCompatibility = "1.8"
		targetCompatibility = "1.8"
	}
	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"
		}
	}
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
}

tasks {
	compileKotlin {
		kotlinOptions.jvmTarget = "1.8"
	}
	compileTestKotlin {
		kotlinOptions.jvmTarget = "1.8"
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "jacoco")

	jacoco {
		toolVersion = "0.8.7"
	}

	tasks.jacocoTestReport {
		reports {
			xml.isEnabled = true
		}
	}

	tasks.test {
		useJUnitPlatform()
		finalizedBy(tasks.jacocoTestReport)
	}
	tasks.jacocoTestReport {
		dependsOn(tasks.test)
	}
}
