package io.fluidsonic.server

import io.fluidsonic.json.*


internal class PropertyInjectingJsonReader(
	properties: Map<String, String>,
	private val source: JsonReader
) : JsonReader {

	private var currentProperty: Map.Entry<String, String>? = null
	private var currentPropertyKeyRead = false
	private var hasReadIsolatedValue = false
	private var isRightAfterCustomProperty = false
	private val propertyIterator = properties.entries.iterator()
	private val rootDepth = source.depth
	private var topLevelIsMap = false
	private var valueIsolationCount = 0


	override fun beginValueIsolation(): JsonDepth {
		val nextToken = nextToken // loads currentProperty

		return if (currentProperty != null) {
			valueIsolationCheck(nextToken != null) { "the root value has already been read" }
			valueIsolationCheck(!isInValueIsolation || !hasReadIsolatedValue) { "cannot begin before previous one has ended" }

			valueIsolationCount += 1

			depth
		}
		else {
			source.beginValueIsolation()
		}
	}


	override fun close() {
		source.close()
	}


	override val depth
		get() = source.depth


	override fun endValueIsolation(depth: JsonDepth) {
		if (currentProperty != null || isRightAfterCustomProperty) {
			valueIsolationCheck(depth <= this.depth) { "lists or maps have been ended prematurely" }
			valueIsolationCheck(this.depth <= depth) { "lists or maps have not been ended properly" }
			valueIsolationCheck(valueIsolationCount > 0) { "cannot end isolation - it either hasn't begun or been ended already" }
			valueIsolationCheck(hasReadIsolatedValue) { "exactly one value has been expected but none was read" }

			val valueIsolationCount = valueIsolationCount - 1
			this.valueIsolationCount = valueIsolationCount

			if (valueIsolationCount == 0) {
				hasReadIsolatedValue = false
			}
		}
		else {
			source.endValueIsolation(depth = depth)
		}
	}


	private fun expectTokenForCustomProperty(expected: JsonToken) {
		currentProperty ?: return

		val token = nextToken
		if (token != expected) {
			throw JsonException.Schema(
				message = "Unexpected $token, expected $expected",
				offset = offset,
				path = path
			)
		}
	}


	override val isInValueIsolation
		get() = valueIsolationCount > 0 || source.isInValueIsolation


	override val nextToken: JsonToken?
		get() {
			val nextToken = source.nextToken
			if (nextToken != JsonToken.mapEnd) return nextToken
			if (depth.value != (rootDepth.value + 1) || !topLevelIsMap) return nextToken

			if (currentProperty == null) {
				if (!propertyIterator.hasNext()) return nextToken
				currentProperty = propertyIterator.next()
				currentPropertyKeyRead = false
			}

			return if (currentPropertyKeyRead)
				JsonToken.stringValue
			else
				JsonToken.mapKey
		}


	override val offset: Int
		get() {
			if (currentProperty == null) {
				return source.offset
			}

			return -1
		}


	override val path: JsonPath
		get() {
			val path = source.path
			val currentProperty = currentProperty ?: return path

			return JsonPath(path.elements + JsonPath.Element.MapKey(currentProperty.key))
		}


	override fun readBoolean(): Boolean {
		expectTokenForCustomProperty(JsonToken.booleanValue)

		return source.readBoolean()
	}


	override fun readDouble(): Double {
		expectTokenForCustomProperty(JsonToken.numberValue)

		return source.readDouble()
	}


	override fun readListEnd() {
		expectTokenForCustomProperty(JsonToken.listEnd)

		source.readListEnd()
	}


	override fun readListStart() {
		expectTokenForCustomProperty(JsonToken.listStart)

		source.readListStart()
	}


	override fun readLong(): Long {
		expectTokenForCustomProperty(JsonToken.numberValue)

		return source.readLong()
	}


	override fun readMapEnd() {
		expectTokenForCustomProperty(JsonToken.mapEnd)

		source.readMapEnd()

		isRightAfterCustomProperty = false

		if (depth == rootDepth) {
			topLevelIsMap = false
		}
	}


	override fun readMapStart() {
		expectTokenForCustomProperty(JsonToken.mapStart)

		if (depth == rootDepth) {
			topLevelIsMap = true
		}

		source.readMapStart()
	}


	override fun readNull(): Nothing? {
		expectTokenForCustomProperty(JsonToken.nullValue)

		return source.readNull()
	}


	override fun readNumber(): Number {
		expectTokenForCustomProperty(JsonToken.numberValue)

		return source.readNumber()
	}


	override fun readString(): String {
		val currentProperty = currentProperty ?: return source.readString()

		valueIsolationCheck(!isInValueIsolation || !hasReadIsolatedValue) { "cannot read more than one value in a context where only one is expected" }

		return if (currentPropertyKeyRead) {
			val value = currentProperty.value
			this.currentProperty = null
			this.currentPropertyKeyRead = false

			isRightAfterCustomProperty = true

			if (valueIsolationCount > 0) {
				hasReadIsolatedValue = true
			}

			value
		}
		else {
			this.currentPropertyKeyRead = true

			isRightAfterCustomProperty = false

			if (valueIsolationCount > 0) {
				hasReadIsolatedValue = true
			}

			currentProperty.key
		}
	}


	override fun terminate() {
		source.terminate()
	}


	private inline fun valueIsolationCheck(value: Boolean, lazyMessage: () -> String) {
		// contract {
		//  returns() implies value
		// }

		if (!value) valueIsolationError(lazyMessage())
	}


	private fun valueIsolationError(message: String): Nothing {
		throw JsonException.Parsing(
			message = "Value isolation failed: $message",
			offset = offset,
			path = path
		)
	}
}
