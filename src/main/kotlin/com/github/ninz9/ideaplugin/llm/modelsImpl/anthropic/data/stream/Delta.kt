package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.stream

data class Delta(
    val text: String,
    val type: String
)