package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONWriter
import com.github.fluidsonic.fluid.json.use
import com.github.fluidsonic.fluid.json.writeIntoMap
import com.github.fluidsonic.fluid.json.writeMapElement
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.WriterContent
import io.ktor.http.withCharset
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.response.ApplicationSendPipeline
import io.ktor.response.respond
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses


class APIFailureProcessing private constructor(
	private val handlers: Map<KClass<*>, suspend PipelineContext<*, ApplicationCall>.(cause: Throwable) -> Unit>
) {

	private suspend fun handleThrowable(throwable: Throwable, context: PipelineContext<*, ApplicationCall>) {
		var unhandledThrowable = throwable
		while (true) {
			val handler = handlerForThrowable(unhandledThrowable) ?: break
			try {
				context.handler(throwable)
				return
			}
			catch (nextThrowable: Throwable) {
				unhandledThrowable = nextThrowable
			}
		}

		handleUnhandledThrowable(throwable, context = context)
	}


	private suspend fun handleUnhandledThrowable(throwable: Throwable, context: PipelineContext<*, ApplicationCall>) {
		when (throwable) {
			is APIFailure -> {
				log.info("API endpoint failed", throwable)

				context.call.respondWithFailure(
					failure = throwable,
					status = HttpStatusCode.BadRequest
				)
			}

			else -> {
				log.error("API endpoint failed", throwable)

				context.call.respondWithFailure(
					failure = APIFailure(
						code = "internal",
						developerMessage = "An internal error occurred.",
						userMessage = APIFailure.genericUserMessage,
						cause = throwable
					),
					status = HttpStatusCode.InternalServerError
				)
			}
		}

		context.finish()
	}


	private fun handlerForThrowable(throwable: Throwable): (suspend PipelineContext<*, ApplicationCall>.(cause: Throwable) -> Unit)? {
		val clazz = throwable::class

		handlers[clazz]?.let { return it }
		clazz.superclasses.forEach { superclass -> handlers[superclass]?.let { return it } }

		return null
	}


	private suspend fun intercept(context: PipelineContext<*, ApplicationCall>) {
		try {
			coroutineScope {
				context.proceed()
			}
		}
		catch (throwable: Throwable) {
			handleThrowable(throwable, context = context)
		}
	}


	private suspend fun ApplicationCall.respondWithFailure(failure: APIFailure, status: HttpStatusCode) {
		if (response.status() == null) {
			response.status(status)
		}

		respond(WriterContent(
			body = {
				JSONWriter.build(this).use { writer ->
					writer.writeIntoMap {
						writeMapElement("error") {
							writeIntoMap {
								writeMapElement("code", string = failure.code)
								writeMapElement("developerMessage", string = failure.developerMessage)
								writeMapElement("userMessage", string = failure.userMessage)
							}
						}
						writeMapElement("status", string = "error")
					}
				}
			},
			contentType = ContentType.Application.Json.withCharset(Charsets.UTF_8)
		))
	}


	companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, APIFailureProcessing> {

		private val log = LoggerFactory.getLogger(APIFailureProcessing::class.java)!!

		override val key = AttributeKey<APIFailureProcessing>(APIFailureProcessing::class.simpleName!!)


		override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): APIFailureProcessing {
			val configuration = Configuration().apply(configure)
			val feature = APIFailureProcessing(
				handlers = configuration.handlers
			)

			pipeline.sendPipeline.intercept(ApplicationSendPipeline.Render) {
				feature.intercept(this)
			}

			pipeline.intercept(ApplicationCallPipeline.Monitoring) {
				feature.intercept(this)
			}

			pipeline.intercept(ApplicationCallPipeline.Fallback) {
				if (call.response.status() == null) {
					call.response.status(HttpStatusCode.NotFound)

					throw APIFailure(
						code = "unknownEndpoint",
						developerMessage = "The endpoint '${call.request.uri}' does not exist or method '${call.request.httpMethod.value}' is not available",
						userMessage = APIFailure.genericUserMessage
					)
				}
			}

			return feature
		}
	}


	class Configuration {

		@PublishedApi
		internal val handlers: MutableMap<KClass<*>, suspend PipelineContext<*, ApplicationCall>.(cause: Throwable) -> Unit> = hashMapOf()


		inline fun <reified E : Exception> handle(noinline handler: suspend PipelineContext<*, ApplicationCall>.(cause: E) -> Unit) {
			@Suppress("UNCHECKED_CAST")
			handlers[E::class] = handler as suspend PipelineContext<*, ApplicationCall>.(cause: Throwable) -> Unit
		}
	}
}
