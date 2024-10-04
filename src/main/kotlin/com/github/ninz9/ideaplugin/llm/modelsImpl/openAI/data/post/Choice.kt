package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.post

data class Choice(
    val finish_reason: String,
    val index: Int,
    val logprobs: Any,
    val message: Message
)
