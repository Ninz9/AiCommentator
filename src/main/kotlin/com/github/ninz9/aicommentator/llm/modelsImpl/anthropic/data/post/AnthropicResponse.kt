package com.github.ninz9.aicommentator.llm.modelsImpl.anthropic.data.post

data class AnthropicResponse(
    val content: List<Content>,
    val id: String,
    val model: String,
    val role: String,
    val stop_reason: String,
    val stop_sequence: Any,
    val type: String,
    val usage: Usage
)