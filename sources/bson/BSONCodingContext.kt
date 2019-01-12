package com.github.fluidsonic.baku


interface BSONCodingContext {

	companion object {

		val empty = object : BSONCodingContext {}
	}
}
