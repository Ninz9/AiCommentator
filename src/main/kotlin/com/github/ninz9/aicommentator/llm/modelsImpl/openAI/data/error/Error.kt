package com.github.ninz9.aicommentator.llm.modelsImpl.openAI.data.error

data class Error(
    val code: String,
    val message: String,
    val `param`: Any,
    val type: String
)