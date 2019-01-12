pluginManagement {
	repositories {
		bintray("kotlin/kotlin-eap")
		gradlePluginPortal()
	}
}


fun RepositoryHandler.bintray(name: String) =
	maven("https://dl.bintray.com/$name")
