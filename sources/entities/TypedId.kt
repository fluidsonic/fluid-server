package io.fluidsonic.server


inline class TypedId(val untyped: EntityId) {

	override fun toString() =
		untyped.toString()
}
