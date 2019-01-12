import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


description = ""
group = "com.github.fluidsonic"
version = "0.9.0"


plugins {
	kotlin("jvm")
	`java-library`
	id("com.github.ben-manes.versions") version DependencyVersions.versions
}

dependencies {
	api(kotlin("stdlib-jdk8"))
	api("com.github.fluidsonic:fluid-json-coding-jdk8:${DependencyVersions.fluid_json}")
	api("com.github.fluidsonic:fluid-mongo:${DependencyVersions.fluid_mongo}")
	api("com.github.fluidsonic:jetpack:${DependencyVersions.jetpack}")
	api("io.ktor:ktor-server-netty:${DependencyVersions.ktor}")

	implementation("ch.qos.logback:logback-classic:${DependencyVersions.logback}")
}

repositories {
	bintray("fluidsonic/maven")
	bintray("kotlin/kotlin-eap")
	bintray("kotlin/kotlinx")
	bintray("kotlin/ktor")
	mavenCentral()
	jcenter()
}

sourceSets {
	getByName("main") {
		java.setSrcDirs(emptyList())
		kotlin.setSrcDirs(listOf("sources"))
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
	withType<KotlinCompile> {
		sourceCompatibility = "1.8"
		targetCompatibility = "1.8"

		kotlinOptions.jvmTarget = "1.8"
	}

	withType<Wrapper> {
		distributionType = Wrapper.DistributionType.ALL
		gradleVersion = "5.1.1"
	}
}


val SourceSet.kotlin
	get() = withConvention(KotlinSourceSet::class) { kotlin }
