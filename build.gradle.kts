import com.github.fluidsonic.fluid.library.*

plugins {
	id("com.github.fluidsonic.fluid-library") version "0.9.21"
	kotlin("jvm") version "1.3.41"
	kotlin("kapt") version "1.3.41"
}

fluidJvmLibrary {
	name = "baku"
	version = "0.9.30"
}

fluidJvmLibraryVariant {
	description = "helps you focus your REST API back-end on the business logic"
	jdk = JvmTarget.jdk8
}

dependencies {
	api(fluid("json-annotations", "0.9.24"))
	api(fluid("json-coding-jdk8", "0.9.24"))
	api(fluid("mongo", "0.9.8"))
	api(fluid("stdlib", "0.9.24"))

	api(ktor("auth-jwt"))
	api(ktor("server-netty"))

	implementation("ch.qos.logback:logback-classic:1.2.1")

	kapt(fluid("json-annotation-processor", "0.9.24"))
}

repositories {
	bintray("kotlin/ktor")
}

// https://youtrack.jetbrains.com/issue/KT-31641
configurations.getByName("kapt") {
	attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "java-runtime"))
}


@Suppress("unused")
fun DependencyHandler.ktor(name: String, version: String = "1.2.2") =
	"io.ktor:ktor-$name:$version"
