import io.fluidsonic.gradle.*

plugins {
	id("io.fluidsonic.gradle") version "1.1.13"
}

fluidLibrary(name = "server", version = "0.11.1")

fluidLibraryModule(description = "helps you focus your REST API back-end on the business logic") {
	publishSingleTargetAsModule()

	language {
		withoutExplicitApi()
	}

	targets {
		jvmJdk8 {
			withJava()

			dependencies {
				api(fluid("country", "0.9.2"))
				api(fluid("currency", "0.9.1"))
				api(fluid("json-annotations", "1.1.1"))
				api(fluid("json-coding-jdk8", "1.1.1"))
				api(fluid("mongo", "1.1.3"))
				api(fluid("stdlib", "0.10.3"))
				api(ktor("auth-jwt"))
				api(ktor("server-netty"))

				implementation("ch.qos.logback:logback-classic:1.2.3")

				kapt(fluid("json-annotation-processor", "1.1.1"))
			}
		}
	}
}

fun ktor(name: String, version: String = "1.4.3") =
	"io.ktor:ktor-$name:$version"
