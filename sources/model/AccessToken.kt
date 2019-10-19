package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class AccessToken(val value: String) {

	override fun toString() = value
}
