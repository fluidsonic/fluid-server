package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


inline val <Transaction : BakuTransaction> JSONEncoder<Transaction>.transaction
	get() = context
