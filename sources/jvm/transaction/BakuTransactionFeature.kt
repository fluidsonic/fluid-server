package io.fluidsonic.server

import io.ktor.application.*
import io.ktor.util.*


internal class BakuTransactionFeature<Context : BakuContext, Transaction : BakuTransaction>(
	private val context: Context,
	private val environment: BakuEnvironment<Context, Transaction>
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: transaction feature")


	@Suppress("UNCHECKED_CAST")
	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.intercept(ApplicationCallPipeline.Setup) {
			call.attributes.put(transactionAttributeKey, environment.createTransaction(context = this@BakuTransactionFeature.context, call = call))
		}
	}


	companion object {

		val transactionAttributeKey = AttributeKey<BakuTransaction>("Baku: transaction")
	}
}
