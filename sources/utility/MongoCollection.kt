package io.fluidsonic.server

import com.mongodb.client.model.*
import com.mongodb.client.model.Filters.*
import io.fluidsonic.mongo.*
import org.bson.conversions.*


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
