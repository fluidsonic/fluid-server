package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object StandardModule : BakuModule<BakuContext, BakuTransaction>() {

	override fun BakuModuleConfiguration<BakuContext, BakuTransaction>.configure() {
		bson(
			CityNameBSONCodec,
			CompanyNameBSONCodec,
			CountryBSONCodec,
			CurrencyBSONCodec,
			DayOfWeekBSONCodec,
			EmailAddressBSONCodec,
			FirstNameBSONCodec,
			FullNameBSONCodec,
			GeoCoordinateBSONCodec,
			LastNameBSONCodec,
			PasswordHashBSONCodec,
			PhoneNumberBSONCodec,
			PostalCodeBSONCodec,
			UrlBSONCodec,
			ZoneIdBSONCodec
		)

		json(
			CountryJSONCodec,
			CurrencyJSONCodec,
			UrlJSONCodec,
			JSONCodecProvider.generated(BakuJSONCodecProvider::class),
			EnumJSONCodecProvider(transformation = EnumJSONTransformation.ToString(EnumJSONTransformation.Case.lowercase_words))
		)
	}
}
