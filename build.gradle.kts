import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


description = ""
group = "com.github.fluidsonic"
version = "0.9.12"


plugins {
	kotlin("jvm")
	`java-library`
	`maven-publish`
	publishing
	id("com.github.ben-manes.versions") version DependencyVersions.versions
	id("com.jfrog.bintray") version DependencyVersions.bintray
}

dependencies {
	api(kotlin("stdlib-jdk8", DependencyVersions.kotlin))
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

configurations.all {
	resolutionStrategy {
		preferProjectModules()
	}
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


// publishing

val javadoc = tasks["javadoc"] as Javadoc
val javadocJar by tasks.creating(Jar::class) {
	archiveClassifier.set("javadoc")
	from(javadoc)
}

val sourcesJar by tasks.creating(Jar::class) {
	archiveClassifier.set("sources")
	from(sourceSets["main"].allSource)
}


configure<BintrayExtension> {
	user = findProperty("bintrayUser") as String?
	key = findProperty("bintrayApiKey") as String?

	setPublications("default")

	pkg.apply {
		repo = "maven"
		name = "baku"
		publicDownloadNumbers = true
		publish = true
		vcsUrl = "https://github.com/fluidsonic/baku"
		websiteUrl = "https://github.com/fluidsonic/baku"
		setLicenses("Apache-2.0")

		version.apply {
			name = project.version as String?
			vcsTag = project.version as String?
		}
	}
}


configure<PublishingExtension> {
	publications {
		create<MavenPublication>("default") {
			from(components["java"])
			artifact(sourcesJar)
		}
	}
}
