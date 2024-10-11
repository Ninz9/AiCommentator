package com.github.ninz9.ideaplugin.formatters

import com.intellij.openapi.components.Service

@Service()
class KotlinDocFormatter: Formatter {

    private val KDOC_PATTERN = Regex(
                "/\\*\\*\\s*" +        // Start of KDoc comment
                "(\\*\\s*.*\\s*)*" +   // Any number of lines starting with *
                "\\*/",                // End of KDoc comment
        RegexOption.DOT_MATCHES_ALL
    )

    private val PARAM_PATTERN = Regex("@param\\s+(\\w+)\\s+")
    private val RETURN_PATTERN = Regex("@return\\s+")
    private val THROWS_PATTERN = Regex("@throws\\s+(\\w+)\\s+")

    override val linePrefix: String
        get() = "*"
    override val commentPrefix: String
        get() = "/**"
    override val commentSuffix: String
        get() = "*/"

    private fun isValidKDoc(comment: String): Boolean {
        return KDOC_PATTERN.matches(comment)
    }


    private fun hasAllParamsDocumented(kdoc: String, paramNames: List<String>): Boolean {
        val documentedParams = PARAM_PATTERN.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return paramNames.all { it in documentedParams }
    }


    private fun hasReturnDocumented(kdoc: String, hasReturnValue: Boolean): Boolean {
        return if (hasReturnValue) {
            RETURN_PATTERN.containsMatchIn(kdoc)
        } else {
            true // No @return needed for Unit/void methods
        }
    }


    fun hasAllExceptionsDocumented(kdoc: String, exceptionNames: List<String>): Boolean {
        val documentedExceptions = THROWS_PATTERN.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return exceptionNames.all { it in documentedExceptions }
    }

    override val newLineTags: Set<String> = setOf("@param", "@return", "@throws", "@exception", "@see", "@since", "@deprecated")


    override fun isValidDoc(
        doc: String,
//        paramNames: List<String>,
//        hasReturnValue: Boolean,
//        exceptionNames: List<String>
    ): Boolean {
        return isValidKDoc(doc)
//              &&  hasAllParamsDocumented(doc, paramNames) &&
//                hasReturnDocumented(doc, hasReturnValue) &&
//                hasAllExceptionsDocumented(doc, exceptionNames)
    }

}




