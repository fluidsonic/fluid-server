package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON
inline class PostalCode(val raw: String) {

	override fun toString() = raw
}
