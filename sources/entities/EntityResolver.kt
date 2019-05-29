package com.github.fluidsonic.baku

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlin.reflect.*


internal class EntityResolver<Transaction : BakuTransaction>(
	private val resolvers: Map<KClass<out EntityId>, suspend Transaction.(ids: Set<EntityId>) -> ReceiveChannel<Entity>>
) {

	suspend fun resolve(ids: Set<EntityId>, transaction: Transaction) =
		ids
			.groupBy { it.factory }
			.map { (factory, ids) ->
				resolvers[factory.idClass]
					?.let { resolve -> transaction.resolve(ids.toSet()) }
					?: GlobalScope.emptyReceiveChannel()
			}
			.toChannel()
			.flatMap { it }
}
