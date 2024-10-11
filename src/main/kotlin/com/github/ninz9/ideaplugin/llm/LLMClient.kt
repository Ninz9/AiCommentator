package com.github.ninz9.ideaplugin.llm

import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import kotlinx.coroutines.flow.Flow

interface LLMClient {
    suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String>

    suspend fun sendRequest(messages: Collection<ModelMessage>): String

    suspend fun generateComment(messages: Collection<ModelMessage>): Flow<String> {
        return sendRequestStream(messages)
    }
}
