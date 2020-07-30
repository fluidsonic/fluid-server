import io.fluidsonic.gradle.*

plugins {
	id("io.fluidsonic.gradle") version "1.1.2"
}

fluidLibrary(name = "server", version = "0.9.39")

fluidLibraryModule(description = "helps you focus your REST API back-end on the business logic") {
	publishSingleTargetAsModule()

	language {
		withoutExplicitApi()
	}

	targets {
		jvm {
			withJava()

			dependencies {
				api(fluid("json-annotations", "1.1.0"))
				api(fluid("json-coding-jdk8", "1.1.0"))
				api(fluid("mongo", "1.1.1"))
				api(fluid("stdlib", "0.10.0"))
				api(ktor("auth-jwt"))
				api(ktor("server-netty"))

				implementation("ch.qos.logback:logback-classic:1.2.3")

				kapt(fluid("json-annotation-processor", "1.1.0"))
			}
		}
	}
}

fun ktor(name: String, version: String = "1.3.2-1.4.0-rc") =
	"io.ktor:ktor-$name:$version"
