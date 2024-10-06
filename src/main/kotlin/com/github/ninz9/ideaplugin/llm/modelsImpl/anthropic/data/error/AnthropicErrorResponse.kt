package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.error

data class AnthropicErrorResponse(
    val error: Error,
    val type: String
)