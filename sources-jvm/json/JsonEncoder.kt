package io.fluidsonic.server

import io.fluidsonic.json.*


inline val <Transaction : BakuTransaction> JsonEncoder<Transaction>.transaction
	get() = context
