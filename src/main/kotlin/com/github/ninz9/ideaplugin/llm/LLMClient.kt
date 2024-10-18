package com.github.ninz9.ideaplugin.llm

import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a client for Language Learning Models (LLM).
 *
 * This client interface provides methods to send requests to LLM services.
 */
interface LLMClient {

    /**
     * Sends a stream of requests to the Language Learning Model (LLM) service.
     *
     * @param messages A collection of messages representing the role and content to be sent to the LLM service.
     * @return A Flow emitting strings, each representing a part of the response from the LLM service.
     */
    suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String>

    /**
     * Sends a request to the Language Learning Model (LLM) service.
     *
     * @param messages A collection of messages representing the role and content to be sent to the LLM service.
     * @return A string representing the response from the LLM service.
     */
    suspend fun sendRequest(messages: Collection<ModelMessage>): String
}
