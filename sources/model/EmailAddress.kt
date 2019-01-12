package com.github.fluidsonic.baku


inline class EmailAddress(val raw: String) {

	fun toLowerCase() =
		EmailAddress(raw.toLowerCase())


	override fun toString() = raw
}
