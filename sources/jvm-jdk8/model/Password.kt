package io.fluidsonic.server

import io.fluidsonic.json.*


@Json(encoding = Json.Encoding.none)
inline class Password(val value: String) {

	override fun toString() =
		"Password(***)"
}
