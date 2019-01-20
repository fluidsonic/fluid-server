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


internal object BakuCommandFailureFeature : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	private val log = LoggerFactory.getLogger(BakuCommandFailureFeature::class.java)!!

	override val key = AttributeKey<Unit>("Baku: command failure feature")


	private suspend fun process(throwable: Throwable, context: PipelineContext<*, ApplicationCall>) {
		when (throwable) {
			is BakuCommandFailure -> {
				log.info("API endpoint failed", throwable)

				context.call.respondWithFailure(
					failure = throwable,
					status = HttpStatusCode.BadRequest
				)
			}

			else -> {
				log.error("API endpoint failed", throwable)

				context.call.respondWithFailure(
					failure = BakuCommandFailure(
						code = "internal",
						developerMessage = "An internal error occurred.",
						userMessage = BakuCommandFailure.genericUserMessage,
						cause = throwable
					),
					status = HttpStatusCode.InternalServerError
				)
			}
		}

		context.finish()
	}


	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.sendPipeline.intercept(ApplicationSendPipeline.Render) {
			intercept(this)
		}

		pipeline.intercept(ApplicationCallPipeline.Monitoring) {
			intercept(this)
		}

		pipeline.intercept(ApplicationCallPipeline.Fallback) {
			if (call.response.status() == null) {
				call.response.status(HttpStatusCode.NotFound)

				throw BakuCommandFailure(
					code = "unknownEndpoint",
					developerMessage = "The endpoint '${call.request.uri}' does not exist or method '${call.request.httpMethod.value}' is not available",
					userMessage = BakuCommandFailure.genericUserMessage
				)
			}
		}
	}


	private suspend fun intercept(context: PipelineContext<*, ApplicationCall>) {
		try {
			coroutineScope {
				context.proceed()
			}
		}
		catch (throwable: Throwable) {
			process(throwable = throwable, context = context)
		}
	}


	private suspend fun ApplicationCall.respondWithFailure(failure: BakuCommandFailure, status: HttpStatusCode) {
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
}
