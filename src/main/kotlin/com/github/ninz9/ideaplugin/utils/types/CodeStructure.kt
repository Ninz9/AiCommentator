package com.github.ninz9.ideaplugin.utils.types

data class CodeStructure(
    val code: String,
    val language: String,
    val paramNames: List<String> = emptyList(),
    val hasReturnValue: Boolean = false,
    val exceptionNames: List<String> = emptyList(),
    val propertyNames : List<String> = emptyList()
)
