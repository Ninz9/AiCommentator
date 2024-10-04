package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.stream

data class StreamOpenAiResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: String
)
