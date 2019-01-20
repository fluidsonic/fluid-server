package com.github.fluidsonic.baku


internal data class BakuCommandResponse(
	val factory: BakuCommandFactory<*, *, *>,
	val result: Any
)
