package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.post

data class OpenAIResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val usage: Usage
)
