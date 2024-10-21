package com.github.ninz9.aicommentator.utils.types

/**
 * Represents the structure of a code block including its content, language, parameter names, return value presence, exception names, and property names.
 *
 * @property code The actual code in string format.
 * @property language The programming language of the code.
 * @property paramNames List of parameter names in the code, defaults to an empty list.
 * @property hasReturnValue Indicates whether the code block has a return value, defaults to false.
 * @property exceptionNames List of exceptions the code might throw, defaults to an empty list.
 * @property propertyNames List of property names in the code, defaults to an empty list.
 */
data class CodeStructure(
    val code: String,
    val language: String,
    val paramNames: List<String> = emptyList(),
    val hasReturnValue: Boolean = false,
    val exceptionNames: List<String> = emptyList(),
    val propertyNames : List<String> = emptyList()
)
