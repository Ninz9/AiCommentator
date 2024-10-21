package com.github.ninz9.aicommentator.llm.modelsImpl.anthropic.data.stream

data class AnthropicStreamResponse(
    val delta: Delta?,
    val index: Int?,
    val type: String
)