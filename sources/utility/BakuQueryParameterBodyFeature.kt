package com.github.fluidsonic.baku

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.httpMethod
import io.ktor.util.AttributeKey
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.io.charsets.Charset


internal object BakuQueryParameterBodyFeature : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	private val allowedMethods = setOf(HttpMethod.Get, HttpMethod.Head)
	private const val parameterName = "body"


	override val key = AttributeKey<Unit>("Baku: query parameter body feature")


	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.receivePipeline.intercept(ApplicationReceivePipeline.Before) { receive ->
			if (!allowedMethods.contains(call.request.httpMethod)) {
				proceed()
				return@intercept
			}

			val body = call.request.queryParameters[parameterName]
			if (body == null) {
				proceed()
				return@intercept
			}

			proceedWith(ApplicationReceiveRequest(
				type = receive.type,
				value = ByteReadChannel(body, Charset.defaultCharset()) // FIXME get charset from request
			))
		}
	}
}
