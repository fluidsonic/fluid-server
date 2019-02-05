package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object StandardModule : BakuModule<BakuContext, BakuTransaction>() {

	override fun BakuModuleConfiguration<BakuContext, BakuTransaction>.configure() {
		bson(
			CityNameBSONCodec,
			CompanyNameBSONCodec,
			CountryBSONCodec,
			CurrencyBSONCodec,
			EmailAddressBSONCodec,
			FirstNameBSONCodec,
			FullNameBSONCodec,
			LastNameBSONCodec,
			PasswordHashBSONCodec,
			PhoneNumberBSONCodec,
			PostalCodeBSONCodec,
			UrlBSONCodec
		)

		json(
			CountryJSONCodec,
			CurrencyJSONCodec,
			UrlJSONCodec,
			JSONCodecProvider.generated(BakuJSONCodecProvider::class),
			EnumJSONCodecProvider(transformation = EnumJSONTransformation.ToString(EnumJSONTransformation.Case.`lowercase words`))
		)
	}
}
