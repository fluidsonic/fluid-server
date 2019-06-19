import com.github.fluidsonic.fluid.library.*

plugins {
	id("com.github.fluidsonic.fluid-library") version "0.9.16"
	kotlin("jvm") version "1.3.31"
	kotlin("kapt") version "1.3.31"
}

fluidJvmLibrary {
	name = "baku"
	version = "0.9.26"
}

fluidJvmLibraryVariant {
	description = "helps you focus your REST API back-end on the business logic"
	jdk = JDK.v1_8
}

dependencies {
	api(fluid("json-annotations", "0.9.22"))
	api(fluid("json-coding-jdk8", "0.9.22"))
	api(fluid("mongo", "0.9.5"))
	api(fluid("stdlib", "0.9.22"))

	api(ktor("auth-jwt"))
	api(ktor("server-netty"))

	implementation("ch.qos.logback:logback-classic:1.2.1")

	kapt(fluid("json-annotation-processor", "0.9.22"))
}

repositories {
	bintray("kotlin/ktor")
}

// https://youtrack.jetbrains.com/issue/KT-31641
configurations.getByName("kapt") {
	attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "java-runtime"))
}


@Suppress("unused")
fun DependencyHandler.ktor(name: String, version: String = "1.2.1") =
	"io.ktor:ktor-$name:$version"
