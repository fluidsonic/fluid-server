import com.github.fluidsonic.fluid.library.*

plugins {
	id("com.github.fluidsonic.fluid-library") version "0.9.3"
	kotlin("kapt") version "1.3.30-eap-11"
}

fluidLibrary {
	name = "baku"
	version = "0.9.20"
}

fluidLibraryVariant {
	jdk = JDK.v1_8
}

dependencies {
	api(fluid("json-annotations", "0.9.15"))
	api(fluid("json-coding-jdk8", "0.9.15"))
	api(fluid("mongo", "0.9.3"))
	api(fluid("stdlib-jdk8", "0.9.1"))

	api(ktor("auth-jwt"))
	api(ktor("server-netty"))

	implementation("ch.qos.logback:logback-classic:1.2.1")

	kapt(fluid("json-annotation-processor", "0.9.15"))
}

repositories {
	bintray("kotlin/ktor")
}


@Suppress("unused")
fun DependencyHandler.ktor(name: String, version: String = "1.1.3") =
	"io.ktor:ktor-$name:$version"
