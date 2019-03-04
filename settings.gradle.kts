pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://dl.bintray.com/fluidsonic/maven")
	}
}

rootProject.name = "baku"

includeBuild("../fluid-json")
