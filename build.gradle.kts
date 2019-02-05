import com.github.fluidsonic.fluid.library.*

plugins {
	id("com.github.fluidsonic.fluid-library") version "0.9.0"
}

fluidLibrary {
	name = "baku"
	version = "0.9.17"
}

fluidLibraryVariant {
	jdk = JDK.v1_8
}

dependencies {
	api(fluid("json-coding-jdk8", "0.9.12"))
	api(fluid("mongo", "0.9.3"))
	api(fluid("stdlib-jdk8", "0.9.1"))

	api(ktor("auth-jwt"))
	api(ktor("server-netty"))

	implementation("ch.qos.logback:logback-classic:1.2.1")
}

repositories {
	bintray("fluidsonic/maven")
	bintray("kotlin/ktor")
}


@Suppress("unused")
fun DependencyHandler.ktor(name: String, version: String = "1.1.2") =
	"io.ktor:ktor-$name:$version"
