package io.fluidsonic.server


internal data class BakuCommandResponse(
	val factory: BakuCommandFactory<*, *, *>,
	val result: Any
)
