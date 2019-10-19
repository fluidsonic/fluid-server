package io.fluidsonic.server


internal interface EntityIdFactoryProvider {

	fun idFactoryForType(type: String): EntityId.Factory<*>?
}
