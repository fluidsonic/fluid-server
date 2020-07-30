package io.fluidsonic.server

import io.fluidsonic.json.*


inline val <Transaction : BakuTransaction> JsonDecoder<Transaction>.transaction
	get() = context
