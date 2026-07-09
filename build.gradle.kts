plugins {
	kotlin("jvm") version "2.4.0"
	kotlin("plugin.serialization") version "2.4.0"
	`maven-publish`
}

group = "dev.lyric"
version = "1.1.2"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
	compileOnly("io.heapy.kotaml:kotaml:0.110.0")
	testImplementation("io.heapy.kotaml:kotaml:0.110.0")
	testImplementation(kotlin("test"))
}

kotlin {
	jvmToolchain(21)
}

java {
	withSourcesJar()
	withJavadocJar()
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = "com.github.Fusezion"
			artifactId = project.name
			version = project.version.toString()

			from(components["java"])
		}
	}
}