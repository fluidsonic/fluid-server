package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.utils.CacheControl
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.*
import org.slf4j.*


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

		pipeline.sendPipeline.intercept(ApplicationSendPipeline.Transform) {
			val subject = subject
			if (subject is UnauthorizedResponse) {
				val response = call.response
				response.status(subject.status ?: HttpStatusCode.Unauthorized)

				for (header in subject.headers.entries()) {
					for (value in header.value) {
						response.headers.append(name = header.key, value = value)
					}
				}

				throw BakuCommandFailure(
					code = "invalidAccessToken",
					developerMessage = "The access token is either invalid or no longer valid.",
					userMessage = "You are no longer signed in. Please sign out and then sign in again."
				)
			}
		}

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

		response.header(HttpHeaders.CacheControl, CacheControl.NO_CACHE)

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
