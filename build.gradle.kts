import io.fluidsonic.gradle.*

plugins {
	id("io.fluidsonic.gradle") version "1.1.18"
}

fluidLibrary(name = "server", version = "0.11.2")

fluidLibraryModule(description = "helps you focus your REST API back-end on the business logic") {
	language {
		version("1.4")
		withoutExplicitApi()
	}

	targets {
		jvm {
			withJava()

			dependencies {
				api(fluid("country", "0.9.3"))
				api(fluid("currency", "0.9.2"))
				api(fluid("json-annotations", "1.1.1"))
				api(fluid("json-coding-jdk8", "1.1.1"))
				api(fluid("mongo", "1.1.3"))
				api(fluid("stdlib", "0.10.4"))
				api(ktor("auth-jwt"))
				api(ktor("server-netty"))

				implementation("ch.qos.logback:logback-classic:1.2.3")

				kapt(fluid("json-annotation-processor", "1.1.1"))
			}
		}
	}
}

fun ktor(name: String, version: String = "1.5.1") =
	"io.ktor:ktor-$name:$version"
