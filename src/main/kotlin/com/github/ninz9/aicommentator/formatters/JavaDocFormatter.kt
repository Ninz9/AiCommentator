package com.github.ninz9.aicommentator.formatters

import com.github.ninz9.aicommentator.utils.types.CodeStructure
import com.intellij.openapi.components.Service

@Service()
class JavaDocFormatter : Formatter {
    private val javaDocPattern = Regex(
        "/\\*\\*\\s*" +        // Start of JavaDoc comment
                "(\\*\\s*.*\\s*)*" +   // Any number of lines starting with *
                "\\*/",                // End of JavaDoc comment
        RegexOption.DOT_MATCHES_ALL
    )

    private val paramPattern = Regex("@param\\s+(\\w+)\\s+")
    private val returnPattern = Regex("@return\\s+")
    private val throwsPattern = Regex("@throws\\s+(\\w+)\\s+")
    private val propertiesPattern = Regex("@property\\s+(\\w+)\\s+")

    override val newLineTags: Set<String> =
        setOf("@param", "@return", "@throws", "@exception", "@see", "@since", "@deprecated", "@property", "@constructor")
    override val linePrefix = "*"
    override val commentPrefix = "/**"
    override val commentSuffix = "*/"

    /**
     * Checks if the given string is a valid JavaDoc comment.
     *
     * @param comment The string to check.
     * @return True if the string is a valid JavaDoc comment, false otherwise.
     */
    private fun isValidJavaDoc(comment: String): Boolean {
        return javaDocPattern.matches(comment)
    }

    /**
     * Checks if all parameters in the method signature are documented in the JavaDoc.
     *
     * @param javadoc The JavaDoc comment to check.
     * @param paramNames List of parameter names from the method signature.
     * @return True if all parameters are documented, false otherwise.
     */
    private fun hasAllParamsDocumented(javadoc: String, paramNames: List<String>): Boolean {
        val documentedParams = paramPattern.findAll(javadoc).map { it.groupValues[1] }.toSet()
        return paramNames.all { it in documentedParams }
    }

    /**
     * Checks if the JavaDoc includes a @return tag when the method returns a value.
     *
     * @param javadoc The JavaDoc comment to check.
     * @param hasReturnValue True if the method returns a value, false for void.
     * @return True if @return is present when needed, false otherwise.
     */
    private fun hasReturnDocumented(javadoc: String, hasReturnValue: Boolean): Boolean {
        return if (hasReturnValue) {
            returnPattern.containsMatchIn(javadoc)
        } else {
            true // No @return needed for void methods
        }
    }

    /**
     * Checks if all exceptions in the method signature are documented in the JavaDoc.
     *
     * @param javadoc The JavaDoc comment to check.
     * @param exceptionNames List of exception names from the method signature.
     * @return True if all exceptions are documented, false otherwise.
     */
    private fun hasAllExceptionsDocumented(javadoc: String, exceptionNames: List<String>): Boolean {
        val documentedExceptions = throwsPattern.findAll(javadoc).map { it.groupValues[1] }.toSet()
        return exceptionNames.all { it in documentedExceptions }
    }


    /**
     * Checks if all specified properties are documented in the JavaDoc.
     *
     * @param javadoc The JavaDoc comment to check.
     * @param propertyNames List of property names that need to be documented.
     * @return True if all properties are documented, false otherwise.
     */
    private fun hasAllPropertiesDocumented(javadoc: String, propertyNames: List<String>): Boolean {
        val documentedProperties = propertiesPattern.findAll(javadoc).map { it.groupValues[1] }.toSet()
        return propertyNames.all { it in documentedProperties }
    }


    /**
     * Validates if the provided JavaDoc is complete and correctly formatted according to the given code structure.
     *
     * @param doc The JavaDoc string to validate.
     * @param codeStructure An instance of CodeStructure containing details about the method or class that the JavaDoc should describe.
     * @return True if the JavaDoc is valid and properly documents all elements, false otherwise.
     */
    override fun isValidDoc(
        doc: String,
       codeStructure: CodeStructure
    ): Boolean {
        return  isValidJavaDoc(doc) &&
                hasAllParamsDocumented(doc, codeStructure.paramNames) &&
                hasReturnDocumented(doc, codeStructure.hasReturnValue) &&
                hasAllExceptionsDocumented(doc, codeStructure.exceptionNames) &&
                hasAllPropertiesDocumented(doc, codeStructure.propertyNames)
    }
}