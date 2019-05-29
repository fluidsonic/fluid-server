package com.github.fluidsonic.baku

import io.ktor.http.*


fun Url.toBuilder() =
	URLBuilder().takeFrom(this)
