package com.github.ninz9.ideaplugin.generators

import com.intellij.openapi.util.NlsSafe

data class CodeStructure(
    val code: String,
    val language: @NlsSafe String,
    val complexity: String
)
