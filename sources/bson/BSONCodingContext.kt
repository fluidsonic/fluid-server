package io.fluidsonic.server


interface BSONCodingContext {

	companion object {

		val empty = object : BSONCodingContext {}
	}
}
