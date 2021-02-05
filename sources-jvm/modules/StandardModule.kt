package io.fluidsonic.server

import io.fluidsonic.json.*


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
			CountryJsonCodec,
			CurrencyJsonCodec,
			UrlJsonCodec,
			JsonCodecProvider.generated(BakuJsonCodecProvider::class),
			EnumJsonCodecProvider(transformation = EnumJsonTransformation.ToString(EnumJsonTransformation.Case.lowercase_words))
		)
	}
}
