plugins {
	kotlin("jvm") version "2.4.0"
	kotlin("plugin.serialization") version "2.3.21"
	id("com.gradleup.shadow") version "9.4.2"
}

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

tasks {

	shadowJar {
		archiveClassifier = ""
		archiveVersion = ""
	}

	build {
		dependsOn(shadowJar)
	}

}
