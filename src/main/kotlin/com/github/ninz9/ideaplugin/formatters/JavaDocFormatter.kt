package com.github.ninz9.ideaplugin.formatters

import com.intellij.openapi.components.Service

@Service()
class JavaDocFormatter : Formatter {
    private val JAVADOC_PATTERN = Regex(
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
        setOf("@param", "@return", "@throws", "@exception", "@see", "@since", "@deprecated", "@property")
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
        return JAVADOC_PATTERN.matches(comment)
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

    private fun hasAllPropertiesDocumented(javadoc: String, propertyNames: List<String>): Boolean {
        val documentedProperties = propertiesPattern.findAll(javadoc).map { it.groupValues[1] }.toSet()
        return propertyNames.all { it in documentedProperties }
    }

    /**
     * Performs a comprehensive check of the JavaDoc against the method signature.
     *
     * @param doc The JavaDoc comment to check.
     * @param paramNames List of parameter names from the method signature.
     * @param hasReturnValue True if the method returns a value, false for void.
     * @param exceptionNames List of exception names from the method signature.
     * @return True if the JavaDoc is valid and complete, false otherwise.
     */
    override fun isValidDoc(
        doc: String,
        paramNames: List<String>,
        hasReturnValue: Boolean,
        exceptionNames: List<String>,
        propertyNames: List<String>
    ): Boolean {
        return  isValidJavaDoc(doc) &&
                hasAllParamsDocumented(doc, paramNames) &&
                hasReturnDocumented(doc, hasReturnValue) &&
                hasAllExceptionsDocumented(doc, exceptionNames) &&
                hasAllPropertiesDocumented(doc, propertyNames)
    }
}