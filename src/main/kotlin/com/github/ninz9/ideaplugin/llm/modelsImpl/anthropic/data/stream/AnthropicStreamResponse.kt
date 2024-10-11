package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.stream

data class AnthropicStreamResponse(
    val delta: Delta?,
    val index: Int?,
    val type: String
)