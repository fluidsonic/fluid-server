package com.github.fluidsonic.baku

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.util.AttributeKey


internal class BakuTransactionFeature<Context : BakuContext, Transaction : BakuTransaction>(
	private val service: BakuService<Context, Transaction>,
	private val context: Context
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: transaction feature")


	@Suppress("UNCHECKED_CAST")
	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.intercept(ApplicationCallPipeline.Setup) {
			call.attributes.put(transactionAttributeKey, service.createTransaction(context = this@BakuTransactionFeature.context, call = call))
		}
	}


	companion object {

		val transactionAttributeKey = AttributeKey<BakuTransaction>("Baku: transaction")
	}
}
