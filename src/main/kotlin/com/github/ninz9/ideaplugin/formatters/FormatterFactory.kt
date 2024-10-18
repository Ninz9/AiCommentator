package com.github.ninz9.ideaplugin.formatters

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

/**
 * A factory service responsible for providing the appropriate formatter
 * instance based on the specified programming language.
 *
 * @service Provides an implementation of the Formatter interface specific to a given language.
 */
@Service
class FormatterFactory {
    fun getFormatter(language: String) : Formatter {
        return when (language) {
            "Java" -> service<JavaDocFormatter>()
            "Kotlin" -> service<KotlinDocFormatter>()
            else -> throw IllegalArgumentException("Unsupported language")
        }
    }
}
