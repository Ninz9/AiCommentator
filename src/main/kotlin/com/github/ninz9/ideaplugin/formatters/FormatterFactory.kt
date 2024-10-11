package com.github.ninz9.ideaplugin.formatters

import com.intellij.openapi.components.service

class FormatterFactory {
    fun getFormatter(language: String) : Formatter {
        return when (language) {
            "Java" -> service<JavaDocFormatter>()
            "Kotlin" -> service<KotlinDocFormatter>()
            else -> throw IllegalArgumentException("Unsupported language")
        }
    }
}
