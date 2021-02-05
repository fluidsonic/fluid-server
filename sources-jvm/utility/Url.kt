package io.fluidsonic.server

import io.ktor.http.*


fun Url.toBuilder() =
	URLBuilder().takeFrom(this)
