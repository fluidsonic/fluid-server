package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.http.content.OutgoingContent
import io.ktor.response.ApplicationSendPipeline
import io.ktor.util.AttributeKey


internal class APIResponseProcessing<Transaction : BakuTransaction>(
	private val codecProvider: JSONCodecProvider<Transaction>,
	private val entityResolver: EntityResolver<Transaction>
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>(APIResponseProcessing::class.qualifiedName!!)


	@Suppress("UNCHECKED_CAST")
	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.sendPipeline.intercept(ApplicationSendPipeline.Render) { subject ->
			if (subject is OutgoingContent) return@intercept

			proceedWith(
				APIResponseProcessor(
					codecProvider = codecProvider,
					entityResolver = entityResolver
				)
					.process(
						payload = subject,
						transaction = transaction as Transaction
					)
			)
		}
	}
}
