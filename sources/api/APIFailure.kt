package com.github.fluidsonic.baku


class APIFailure(
	val code: String,
	val developerMessage: String,
	val userMessage: String,
	cause: Throwable? = null
) : Exception(developerMessage, cause) {

	companion object {

		const val genericUserMessage = "Looks like we're having some trouble right now.\nPlease try again soon."
	}
}
