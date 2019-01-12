package com.github.fluidsonic.baku

import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.reflect.KClass


class BakuEntityResolution<Transaction : BakuTransaction> internal constructor() {

	@PublishedApi
	internal val resolvers: MutableMap<KClass<out EntityId>, suspend Transaction.(ids: Set<EntityId>) -> ReceiveChannel<Entity>> =
		mutableMapOf()


	inline fun <reified Id : EntityId> resolve(noinline resolver: suspend Transaction.(ids: Set<Id>) -> ReceiveChannel<Entity>) {
		@Suppress("UNCHECKED_CAST")
		if (resolvers.putIfAbsent(Id::class, resolver as suspend Transaction.(ids: Set<EntityId>) -> ReceiveChannel<Entity>) != null) {
			error("Cannot register multiple entity resolvers for ${Id::class}")
		}
	}
}
