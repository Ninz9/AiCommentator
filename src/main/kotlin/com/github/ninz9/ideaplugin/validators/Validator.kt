package com.github.ninz9.ideaplugin.validators

interface Validator {
    fun isValidDoc(doc: String,
                   paramNames: List<String>,
                   hasReturnValue: Boolean,
                   exceptionNames: List<String>): Boolean
}
