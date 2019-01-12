package com.github.fluidsonic.baku


internal object StandardModule : BakuModule<BakuContext, BakuTransaction>() {

	override fun BakuModuleConfiguration<BakuContext, BakuTransaction>.configure() {
		bsonCodecProviders(
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

		jsonCodecProviders(
			AccessTokenJSONCodec,
			APIRequestJSONCodec,
			CityNameJSONCodec,
			CompanyNameJSONCodec,
			CountryJSONCodec,
			CurrencyJSONCodec,
			EmailAddressJSONCodec,
			FirstNameJSONCodec,
			FullNameJSONCodec,
			LastNameJSONCodec,
			PasswordJSONCodec,
			PhoneNumberJSONCodec,
			PostalCodeJSONCodec,
			RefreshTokenJSONCodec,
			UrlJSONCodec
		)
	}
}
