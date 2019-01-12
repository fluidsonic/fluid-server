package com.github.fluidsonic.baku


inline class TypedId(val untyped: EntityId) {

	override fun toString() =
		untyped.toString()
}
