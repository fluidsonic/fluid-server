package com.github.fluidsonic.baku

import io.ktor.http.URLBuilder
import io.ktor.http.encodeURLQueryComponent


fun URLBuilder.appendPath(vararg components: String) =
	appendPath(components.toList())


fun URLBuilder.appendPath(components: List<String>): URLBuilder {
	encodedPath += components.joinToString(
		separator = "/",
		prefix = if (encodedPath.endsWith('/')) "" else "/"
	) { it.encodeURLQueryComponent() }

	return this
}
