package com.github.ninz9.ideaplugin.utils.types


data class MethodStructure(
    val code: String,
    val language: String,
    val complexity: String,
    val paramNames: List<String>,
    val hasReturnValue: Boolean,
    val exceptionNames: List<String>
)
