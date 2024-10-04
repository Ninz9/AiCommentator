package com.github.ninz9.ideaplugin.validators

class KotlinDocValidator: Validator {
    private val KDOC_PATTERN = Regex(
        "/\\*\\*\\s*" +        // Start of KDoc comment
                "(\\*\\s*.*\\s*)*" +   // Any number of lines starting with *
                "\\*/",                // End of KDoc comment
        RegexOption.DOT_MATCHES_ALL
    )

    private val PARAM_PATTERN = Regex("@param\\s+(\\w+)\\s+")
    private val RETURN_PATTERN = Regex("@return\\s+")
    private val THROWS_PATTERN = Regex("@throws\\s+(\\w+)\\s+")

    /**
     * Checks if the given string is a valid KDoc comment.
     *
     * @param comment The string to check.
     * @return True if the string is a valid KDoc comment, false otherwise.
     */
    private fun isValidKDoc(comment: String): Boolean {
        return KDOC_PATTERN.matches(comment)
    }

    /**
     * Checks if all parameters in the method signature are documented in the KDoc.
     *
     * @param kdoc The KDoc comment to check.
     * @param paramNames List of parameter names from the method signature.
     * @return True if all parameters are documented, false otherwise.
     */
    private fun hasAllParamsDocumented(kdoc: String, paramNames: List<String>): Boolean {
        val documentedParams = PARAM_PATTERN.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return paramNames.all { it in documentedParams }
    }

    /**
     * Checks if the KDoc includes a @return tag when the method returns a value.
     *
     * @param kdoc The KDoc comment to check.
     * @param hasReturnValue True if the method returns a value, false for Unit/void.
     * @return True if @return is present when needed, false otherwise.
     */
    private fun hasReturnDocumented(kdoc: String, hasReturnValue: Boolean): Boolean {
        return if (hasReturnValue) {
            RETURN_PATTERN.containsMatchIn(kdoc)
        } else {
            true // No @return needed for Unit/void methods
        }
    }

    /**
     * Checks if all exceptions in the method signature are documented in the KDoc.
     *
     * @param kdoc The KDoc comment to check.
     * @param exceptionNames List of exception names from the method signature.
     * @return True if all exceptions are documented, false otherwise.
     */
    fun hasAllExceptionsDocumented(kdoc: String, exceptionNames: List<String>): Boolean {
        val documentedExceptions = THROWS_PATTERN.findAll(kdoc).map { it.groupValues[1] }.toSet()
        return exceptionNames.all { it in documentedExceptions }
    }

    /**
     * Performs a comprehensive check of the KDoc against the method signature.
     *
     * @param doc The KDoc comment to check.
     * @param paramNames List of parameter names from the method signature.
     * @param hasReturnValue True if the method returns a value, false for Unit/void.
     * @param exceptionNames List of exception names from the method signature.
     * @return True if the KDoc is valid and complete, false otherwise.
     */
    override fun isValidDoc(
        doc: String,
        paramNames: List<String>,
        hasReturnValue: Boolean,
        exceptionNames: List<String>
    ): Boolean {
        return isValidKDoc(doc) &&
                hasAllParamsDocumented(doc, paramNames) &&
                hasReturnDocumented(doc, hasReturnValue) &&
                hasAllExceptionsDocumented(doc, exceptionNames)
    }
}

