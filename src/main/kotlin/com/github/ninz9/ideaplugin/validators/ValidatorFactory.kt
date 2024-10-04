package com.github.ninz9.ideaplugin.validators

class ValidatorFactory {
    fun getValidator(language: String) {
        when (language) {
            "Java" -> JavaDocValidator()
            "Kotlin" -> KotlinDocValidator()
            else -> throw IllegalArgumentException("Unsupported language")
        }
    }
}
