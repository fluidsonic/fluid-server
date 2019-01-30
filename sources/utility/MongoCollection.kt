package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.mongo.*
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import org.bson.conversions.Bson


suspend fun <TDocument : Any> MongoCollection<TDocument>.findOneByIdAndUpdate(
	id: Any,
	update: Bson,
	options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
) =
	findOneAndUpdate(
		filter = eq("_id", id),
		update = update,
		options = options
	)
