package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import com.github.fluidsonic.fluid.json.JSONCodingParser
import com.github.fluidsonic.fluid.json.JSONCodingTypeReference
import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.jsonCodingType
import com.github.fluidsonic.fluid.json.parseValueOfTypeOrNull
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.http.ContentType
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.jvm.javaio.toInputStream
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


internal class JSONConverter<Transaction : BakuTransaction>(
	private val jsonCodecProvider: JSONCodecProvider<Transaction>
) : ContentConverter {

	override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any) =
		error("Should've been handled by APIResponseProcessing")


	@Suppress("UNCHECKED_CAST")
	override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
		val request = context.subject
		val content = request.value as? ByteReadChannel ?: return null

		val requestType = request.type
		val valueType = if (requestType.isSubclassOf(JSONCodingTypeReference::class)) {
			jsonCodingType(requestType as KClass<JSONCodingTypeReference<*>>)
		}
		else {
			jsonCodingType(request.type)
		}

		val parser = JSONCodingParser.builder(context.transaction as Transaction)
			.decodingWith(jsonCodecProvider)
			.build()

		return try {
			parser.parseValueOfTypeOrNull(
				source = content.toInputStream().reader(charset = context.call.request.contentCharset() ?: Charsets.UTF_8),
				valueType = valueType
			)
		}
		catch (e: JSONException) {
			throw APIFailure(
				code = "invalidRequest",
				developerMessage = e.message ?: "Unable to process JSON",
				userMessage = APIFailure.genericUserMessage,
				cause = e
			)
		}
	}


	companion object {

		private val log = LoggerFactory.getLogger(JSONConverter::class.java)!!
	}
}


suspend inline fun <reified Type : Any> ApplicationCall.receiveJSON(): Type {
	val type = object : JSONCodingTypeReference<Type>() {}::class
	val incomingContent = request.receiveChannel()
	val receiveRequest = ApplicationReceiveRequest(type, incomingContent)
	val transformed = request.pipeline.execute(this, receiveRequest).value

	@Suppress("UNCHECKED_CAST")
	return transformed as Type
}
