package com.github.ninz9.ideaplugin.formatters

import com.intellij.openapi.components.Service

@Service()
class KotlinDocFormatter : Formatter {

    private val KDOC_PATTERN = Regex(
        "/\\*\\*\\s*" +        // Start of KDoc comment
                "(\\*\\s*.*\\s*)*" +   // Any number of lines starting with *
                "\\*/",                // End of KDoc comment
        RegexOption.DOT_MATCHES_ALL
    )

    override val newLineTags: Set<String> =
        setOf("@param", "@return", "@throws", "@exception", "@see", "@since", "@deprecated", "@property")


    private val paramPattern = Regex("@param\\s+(\\w+)\\s+")
    private val returnPattern = Regex("@return\\s+")
    private val throwsPattern = Regex("@throws\\s+(\\w+)\\s+")
    private val propertiesPattern = Regex("@property\\s+(\\w+)\\s+")

    override val linePrefix = "*"
    override val commentPrefix = "/**"
    override val commentSuffix = "*/"

    private fun isValidKDoc(comment: String): Boolean {
        return KDOC_PATTERN.matches(comment)
    }

    private fun hasAllParamsDocumented(kdoc: String, paramNames: List<String>): Boolean {
        val documentedParams = paramPattern.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return paramNames.all { it in documentedParams }
    }

    private fun hasReturnDocumented(kdoc: String, hasReturnValue: Boolean): Boolean {
        return if (hasReturnValue) {
            returnPattern.containsMatchIn(kdoc)
        } else {
            true // No @return needed for Unit/void methods
        }
    }

    fun hasAllExceptionsDocumented(kdoc: String, exceptionNames: List<String>): Boolean {
        val documentedExceptions = throwsPattern.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return exceptionNames.all { it in documentedExceptions }
    }

    fun hasAllPropertiesDocumented(kdoc: String, propertyNames: List<String>): Boolean {
        val documentedProperties = propertiesPattern.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return propertyNames.all { it in documentedProperties }
    }

    override fun isValidDoc(
        doc: String,
        paramNames: List<String>,
        hasReturnValue: Boolean,
        exceptionNames: List<String>,
        propertyNames: List<String>
    ): Boolean {
        return isValidKDoc(doc) &&
                hasAllParamsDocumented(doc, paramNames) &&
                hasReturnDocumented(doc, hasReturnValue) &&
                hasAllExceptionsDocumented(doc, exceptionNames) &&
                hasAllPropertiesDocumented(doc, propertyNames)
    }
}




