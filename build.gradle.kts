import com.github.fluidsonic.fluid.library.*
import org.jetbrains.kotlin.gradle.plugin.*

plugins {
	id("com.github.fluidsonic.fluid-library") version "0.9.25"
	kotlin("jvm") version "1.3.50"
	kotlin("kapt") version "1.3.50"
}

fluidJvmLibrary {
	name = "baku"
	version = "0.9.32"
}

fluidJvmLibraryVariant {
	description = "helps you focus your REST API back-end on the business logic"
	jdk = JvmTarget.jdk8
}

dependencies {
	api(fluid("json-annotations", "0.9.25"))
	api(fluid("json-coding-jdk8", "0.9.25"))
	api(fluid("mongo", "0.9.9"))
	api(fluid("stdlib", "0.9.25")) {
		attributes {
			attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
			attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
		}
	}

	api(ktor("auth-jwt"))
	api(ktor("server-netty"))

	implementation("ch.qos.logback:logback-classic:1.2.1")

	kapt(fluid("json-annotation-processor", "0.9.25"))
}

repositories {
	bintray("kotlin/ktor")
}

configurations {
	all {
		// https://youtrack.jetbrains.com/issue/KT-31641
		// https://youtrack.jetbrains.com/issue/KT-33206

		if (name.contains("kapt"))
			attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
	}
}


@Suppress("unused")
fun DependencyHandler.ktor(name: String, version: String = "1.2.3") =
	"io.ktor:ktor-$name:$version"
