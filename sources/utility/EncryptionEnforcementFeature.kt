package com.github.fluidsonic.baku

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.util.*


internal object EncryptionEnforcementFeature : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: encryption enforcement feature")


	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.intercept(ApplicationCallPipeline.Features) {
			if (call.request.origin.scheme != "https")
				throw BakuCommandFailure(
					code = "encryptedConnectionRequired",
					userMessage = BakuCommandFailure.genericUserMessage,
					developerMessage = "This API must only be used over an encrypted connection."
				)
		}
	}
}
