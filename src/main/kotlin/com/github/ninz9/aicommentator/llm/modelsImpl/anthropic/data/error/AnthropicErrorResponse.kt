package com.github.ninz9.aicommentator.llm.modelsImpl.anthropic.data.error

data class AnthropicErrorResponse(
    val error: Error,
    val type: String
)