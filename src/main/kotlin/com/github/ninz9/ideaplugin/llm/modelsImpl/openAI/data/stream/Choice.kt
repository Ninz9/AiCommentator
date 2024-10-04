package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.stream

data class Choice(
    val delta: Delta,
    val finish_reason: Any,
    val index: Int,
    val logprobs: Any
)
