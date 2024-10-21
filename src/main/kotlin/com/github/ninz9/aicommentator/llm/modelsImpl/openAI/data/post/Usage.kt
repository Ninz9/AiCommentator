package com.github.ninz9.aicommentator.llm.modelsImpl.openAI.data.post

data class Usage(
    val completion_tokens: Int,
    val completion_tokens_details: CompletionTokensDetails,
    val prompt_tokens: Int,
    val total_tokens: Int
)
