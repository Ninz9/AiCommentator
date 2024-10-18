package com.github.ninz9.ideaplugin.formatters

import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.intellij.openapi.components.Service

@Service()
class KotlinDocFormatter : Formatter {

    private val kDocPattern = Regex(
        "/\\*\\*\\s*" +        // Start of KDoc comment
                "(\\*\\s*.*\\s*)*" +   // Any number of lines starting with *
                "\\*/",                // End of KDoc comment
        RegexOption.DOT_MATCHES_ALL
    )

    override val newLineTags: Set<String> =
        setOf("@param", "@return", "@throws", "@exception", "@see", "@since", "@deprecated", "@property", "@constructor")


    private val paramPattern = Regex("@param\\s+(\\w+)\\s+")
    private val returnPattern = Regex("@return\\s+")
    private val propertiesPattern = Regex("@property\\s+(\\w+)\\s+")

    override val linePrefix = "*"
    override val commentPrefix = "/**"
    override val commentSuffix = "*/"


    private fun isValidKDoc(comment: String): Boolean {
        return kDocPattern.matches(comment)
    }

    /**
     * Checks if all parameters listed in a method's signature are documented in the provided KDoc string.
     *
     * @param kdoc The KDoc string to check for parameter documentation.
     * @param paramNames The list of parameter names that should be documented.
     * @return True if all parameter names are found in the KDoc string, false otherwise.
     */
    private fun hasAllParamsDocumented(kdoc: String, paramNames: List<String>): Boolean {
        val documentedParams = paramPattern.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return paramNames.all { it in documentedParams }
    }

    /**
     * Checks if the KDoc string contains a @return tag when necessary.
     *
     * @param kdoc The KDoc string to check.
     * @param hasReturnValue Indicates whether the method has a return value that needs to be documented.
     * @return True if the return value is properly documented when necessary, false otherwise.
     */
    private fun hasReturnDocumented(kdoc: String, hasReturnValue: Boolean): Boolean {
        return if (hasReturnValue) {
            returnPattern.containsMatchIn(kdoc)
        } else {
            true // No @return needed for Unit/void methods
        }
    }

    /**
     * Checks if all properties in the given list are documented in the provided KDoc string.
     *
     * @param kdoc The KDoc string to search for property documentation.
     * @param propertyNames The list of property names that should be documented.
     * @return True if all property names are found in the KDoc string, false otherwise.
     */
    fun hasAllPropertiesDocumented(kdoc: String, propertyNames: List<String>): Boolean {
        val documentedProperties = propertiesPattern.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return propertyNames.all { it in documentedProperties }
    }


    /**
     * Validates the provided KDoc string to ensure it adheres to the expected documentation standards
     * by checking the presence of parameter and return value documentation.
     *
     * @param doc The KDoc string to validate.
     * @param codeStructure The structure of the code to validate against, containing parameter names, return value presence, and property names.
     * @return True if the KDoc string is valid according to the given code structure, false otherwise.
     */
    override fun isValidDoc(
        doc: String,
        codeStructure: CodeStructure
    ): Boolean {
        return isValidKDoc(doc) &&
                hasAllParamsDocumented(doc, codeStructure.paramNames) &&
                hasReturnDocumented(doc, codeStructure.hasReturnValue) &&
                hasAllPropertiesDocumented(doc, codeStructure.propertyNames)
    }
}




