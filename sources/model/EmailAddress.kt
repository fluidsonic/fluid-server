package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON
inline class EmailAddress(val raw: String) {

	fun toLowerCase() =
		EmailAddress(raw.toLowerCase())


	override fun toString() = raw
}
