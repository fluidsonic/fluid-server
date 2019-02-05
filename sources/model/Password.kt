package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON(encoding = JSON.Encoding.none)
inline class Password(val raw: String) {

	override fun toString() =
		"Password(***)"
}
