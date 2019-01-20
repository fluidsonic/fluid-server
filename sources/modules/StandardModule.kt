package com.github.fluidsonic.baku


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
			AccessTokenJSONCodec,
			CityNameJSONCodec,
			CompanyNameJSONCodec,
			CountryJSONCodec,
			CurrencyJSONCodec,
			EmailAddressJSONCodec,
			FirstNameJSONCodec,
			FullNameJSONCodec,
			GeoCoordinateJSONCodec,
			LastNameJSONCodec,
			PasswordJSONCodec,
			PhoneNumberJSONCodec,
			PostalCodeJSONCodec,
			RefreshTokenJSONCodec,
			UrlJSONCodec
		)
	}
}
