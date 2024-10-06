package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic

import com.github.ninz9.ideaplugin.generators.ModelMessage
import com.github.ninz9.ideaplugin.llm.LLMClient
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.error.AnthropicErrorResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.post.AnthropicResponse
import com.github.ninz9.ideaplugin.utils.ApiResponse
import com.github.ninz9.ideaplugin.utils.HttpRequestHelper
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class AnthropicClient(
    private val token: String,
    private val model: String,
    private val maxTokens: Int,
    private val temperature: Float
): LLMClient {


    val url = "https://api.anthropic.com/v1/messages"

    override suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun sendRequest(messages: Collection<ModelMessage>): String {

        val requestBody = buildJsonRequestBody(messages, false)
        val headers = mapOf(
            "anthropic-version" to "2023-06-01",
            "x-api-key" to token,
            "content-Type" to "application/json"
            )

        val response = HttpRequestHelper().post(
            url,
            requestBody,
            headers,
            AnthropicResponse::class.java,
            AnthropicErrorResponse::class.java
        )

        when(response) {
            is ApiResponse.Error -> throw Exception(response.error.error.message)
            is ApiResponse.Success -> return response.data.content.first().text
        }
    }


    private fun buildJsonRequestBody(messages: Collection<ModelMessage>, stream: Boolean): JSONObject {
        val json = JSONObject()
        val messagesJson = messages.map {
            JSONObject().apply {
                put("role", it.role)
                put("content", it.message)
            }
        }
        json.put("messages", messagesJson)
        json.put("model", model)
        json.put("max_tokens", maxTokens)
        json.put("temperature", temperature)
        json.put("stream", stream)
        return json
    }

}
