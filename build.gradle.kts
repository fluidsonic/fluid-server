import io.fluidsonic.gradle.*
import org.jetbrains.kotlin.gradle.plugin.*

plugins {
	id("io.fluidsonic.gradle") version "1.0.7"
	kotlin("jvm") version "1.3.61"
	kotlin("kapt") version "1.3.61"
}

fluidJvmLibrary(name = "server", version = "0.9.39")

fluidJvmLibraryVariant(JvmTarget.jdk8) {
	description = "helps you focus your REST API back-end on the business logic"
}

dependencies {
	api(fluid("json-annotations", "1.0.2"))
	api(fluid("json-coding-jdk8", "1.0.2"))
	api(fluid("mongo", "1.0.0-beta.4"))
	api(fluid("stdlib", "0.9.29")) {
		attributes {
			attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
			attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
		}
	}

	api(ktor("auth-jwt"))
	api(ktor("server-netty"))

	implementation("ch.qos.logback:logback-classic:1.2.3")

	kapt(fluid("json-annotation-processor", "1.0.2"))
}

repositories {
	bintray("kotlin/ktor")
}


@Suppress("unused")
fun DependencyHandler.ktor(name: String, version: String = "1.3.0") =
	"io.ktor:ktor-$name:$version"
