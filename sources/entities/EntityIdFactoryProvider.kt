package com.github.fluidsonic.baku


internal interface EntityIdFactoryProvider {

	fun idFactoryForType(type: String): EntityId.Factory<*>?
}
