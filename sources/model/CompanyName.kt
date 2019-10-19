package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class CompanyName(val value: String) {

	override fun toString() = value
}
