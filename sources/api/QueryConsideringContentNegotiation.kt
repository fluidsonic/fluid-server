package com.github.fluidsonic.baku

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.features.ContentNegotiation
import io.ktor.features.UnsupportedMediaTypeException
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.HttpStatusCodeContent
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.transformDefaultContent
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.acceptItems
import io.ktor.request.contentType
import io.ktor.request.httpMethod
import io.ktor.response.ApplicationSendPipeline
import io.ktor.response.respond
import io.ktor.util.AttributeKey
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.io.charsets.Charset


internal class QueryConsideringContentNegotiation private constructor(
	private val allowedMethods: Set<HttpMethod>,
	private val converters: List<ContentNegotiation.ConverterRegistration>,
	private val parameterName: String
) {

	class Configuration {

		var allowedMethods = setOf(HttpMethod.Get)
		var converters = emptyList<ContentNegotiation.ConverterRegistration>()
		var parameterName = "entity"


		fun converters(configure: ContentNegotiation.Configuration.() -> Unit) {
			converters = ContentNegotiation.install(ApplicationCallPipeline(), configure).registrations
		}
	}


	companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, QueryConsideringContentNegotiation> {

		override val key = AttributeKey<QueryConsideringContentNegotiation>("QueryConsideringContentNegotiation")


		override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): QueryConsideringContentNegotiation {
			val configuration = Configuration().apply(configure)
			val feature = QueryConsideringContentNegotiation(
				allowedMethods = configuration.allowedMethods.toSet(),
				converters = configuration.converters.toList(),
				parameterName = configuration.parameterName
			)

			pipeline.receivePipeline.intercept(ApplicationReceivePipeline.Before) { receive ->
				if (!feature.allowedMethods.contains(call.request.httpMethod)) {
					proceed()
					return@intercept
				}

				val entity = call.request.queryParameters["entity"]
				if (entity == null) {
					proceed()
					return@intercept
				}

				val value = ByteReadChannel(entity, Charset.defaultCharset()) // FIXME get charset from request
				proceedWith(ApplicationReceiveRequest(type = receive.type, value = value))
			}

			// --> from ContentNegotiation.kt

			// Respond with "415 Unsupported Media Type" if content cannot be transformed on receive
			pipeline.intercept(ApplicationCallPipeline.Features) {
				try {
					proceed()
				}
				catch (e: UnsupportedMediaTypeException) {
					call.respond(HttpStatusCode.UnsupportedMediaType)
				}
			}

			pipeline.sendPipeline.intercept(ApplicationSendPipeline.Render) { subject ->
				if (subject is OutgoingContent) return@intercept

				val acceptItems = call.request.acceptItems()
				val suitableConverters = if (acceptItems.isEmpty()) {
					// all converters are suitable since client didn't indicate what it wants
					feature.converters
				}
				else {
					// select converters that match specified Accept header, in order of quality
					acceptItems.flatMap { (contentType, _) ->
						feature.converters.filter { it.contentType.match(contentType) }
					}.distinct()
				}

				// Pick the first one that can convert the subject successfully
				val converted = suitableConverters.mapFirstNotNull {
					it.converter.convertForSend(this, it.contentType, subject)
				}

				val rendered = converted?.let { transformDefaultContent(it) }
					?: HttpStatusCodeContent(HttpStatusCode.NotAcceptable)
				proceedWith(rendered)
			}

			pipeline.receivePipeline.intercept(ApplicationReceivePipeline.Transform) { receive ->
				if (subject.value !is ByteReadChannel) return@intercept
				val contentType = call.request.contentType().withoutParameters()
				val suitableConverter = feature.converters.firstOrNull { it.contentType.match(contentType) }
					?: throw UnsupportedMediaTypeException(contentType)
				val converted = suitableConverter.converter.convertForReceive(this)
					?: throw UnsupportedMediaTypeException(contentType)
				proceedWith(ApplicationReceiveRequest(receive.type, converted))
			}

			// <-- from ContentNegotiation.kt

			return feature
		}
	}
}


// from ContentNegotiation.kt
private inline fun <F, T> Iterable<F>.mapFirstNotNull(block: (F) -> T?): T? {
	@Suppress("LoopToCallChain")
	for (element in this) {
		val mapped = block(element)
		if (mapped != null)
			return mapped
	}
	return null
}
