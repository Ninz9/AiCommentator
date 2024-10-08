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
    private val model: AvailableAnthropicModels,
    private val maxTokens: Int,
    private val temperature: Double
): LLMClient {


    private val url = "https://api.anthropic.com/v1/messages"

    override suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun sendRequest(messages: Collection<ModelMessage>): String {

        val requestBody = buildJsonRequestBody(messageAdapter(messages), false)
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
            is ApiResponse.Success -> {
                if (response.data.content.isEmpty()) {
                    throw Exception("No messages returned")
                }
                return response.data.content.first().text
            }
        }
    }


    private fun buildJsonRequestBody(messages: Map<String, Collection<String>>, stream: Boolean): JSONObject {
        val json = JSONObject()
        val messagesJson = listOf(
            JSONObject().put("role", "user").put("content", messages["user"]?.map {
                JSONObject().put("type", "text").put("text", it)
            } ?: listOf<String>()),
        )
        json.put("system", messages["assistant"]?.first() ?: "")
        json.put("messages", messagesJson)
        json.put("model", model.modelName)
        json.put("max_tokens", maxTokens)
        json.put("temperature", temperature)
        json.put("stream", stream)
        return json
    }

    private fun  messageAdapter(messages: Collection<ModelMessage>): Map<String, Collection<String>> {
        val mergedMessages = mutableMapOf<String, MutableList<String>>()
        messages.forEach {
            if (mergedMessages.containsKey(it.role)) {
                mergedMessages[it.role] = mergedMessages[it.role]!!.apply { add(it.message) }
            } else {
                mergedMessages[it.role] = mutableListOf(it.message)
            }
        }
        return mergedMessages
    }

}
