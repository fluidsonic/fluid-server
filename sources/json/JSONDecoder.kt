package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


inline val <Transaction : BakuTransaction> JSONDecoder<Transaction>.transaction
	get() = context
