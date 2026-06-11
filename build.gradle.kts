import org.gradle.internal.execution.caching.CachingState.enabled

plugins {
	kotlin("jvm") version "2.4.0"
	kotlin("plugin.serialization") version "2.4.0"
	id("com.gradleup.shadow") version "9.4.2"
	`maven-publish`
}

group = "dev.lyric"
version = "1.0.0"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
	implementation("net.mamoe.yamlkt:yamlkt:0.13.0")
}

kotlin {
	jvmToolchain(21)
}

java {
	withSourcesJar()
}

tasks {

	shadowJar {
		archiveClassifier = ""
		minimize()
	}

	build {
		dependsOn(shadowJar)
	}

	jar {
		enabled = false
	}

}

publishing {
	publications {
		create<MavenPublication>("maven") {
			artifact(tasks.shadowJar.get())
			artifact(tasks.named("sourcesJar"))
		}
	}
}
