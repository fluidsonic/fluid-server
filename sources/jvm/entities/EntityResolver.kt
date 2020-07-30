package io.fluidsonic.server

import kotlinx.coroutines.flow.*
import kotlin.reflect.*


internal class EntityResolver<Transaction : BakuTransaction>(
	private val resolvers: Map<KClass<out EntityId>, suspend Transaction.(ids: Set<EntityId>) -> Flow<Entity>>
) {

	suspend fun resolve(ids: Set<EntityId>, transaction: Transaction) =
		ids
			.groupBy { it.factory }
			.map { (factory, ids) ->
				resolvers[factory.idClass]
					?.let { resolve -> transaction.resolve(ids.toSet()) }
					?: emptyFlow()
			}
			.asFlow()
			.flattenConcat()
}
