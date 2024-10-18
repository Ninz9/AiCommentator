package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI

import com.github.ninz9.ideaplugin.llm.LLMClient
import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.error.OpenAiErrorResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.post.OpenAIResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.stream.StreamOpenAiResponse
import com.github.ninz9.ideaplugin.utils.ApiResponse
import com.github.ninz9.ideaplugin.utils.HttpRequestHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class OpenAiClient(
    var token: String,
    val model: AvailableOpenAIModels,
    val maxToken: Int,
    val temperature: Double
) : LLMClient {

    val url = "https://api.openai.com/v1/chat/completions"
    val authHeader = "Bearer $token"

    override suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String> {

        val requestBody = buildJsonRequestBody(messages, true)

        return HttpRequestHelper().stream(
            url,
            requestBody,
            mapOf("Authorization" to authHeader),
            StreamOpenAiResponse::class.java,
            OpenAiErrorResponse::class.java
        ).map {
            when (it) {
                is ApiResponse.Success -> it.data.choices.first().delta?.content ?: ""
                is ApiResponse.Error -> throw Exception(it.error.error.message)
            }
        }
    }


    override suspend fun sendRequest(messages: Collection<ModelMessage>): String {

        val requestBody = buildJsonRequestBody(messages, false)


        val response = HttpRequestHelper().post(
            url,
            requestBody,
            mapOf("Authorization" to authHeader),
            OpenAIResponse::class.java,
            OpenAiErrorResponse::class.java
        )

        when (response) {
            is ApiResponse.Error -> throw Exception(response.error.error.message)
            is ApiResponse.Success -> return response.data.choices.first().message.content
        }
    }

    private fun buildJsonRequestBody(messages: Collection<ModelMessage>, isStreamRequest: Boolean): JSONObject {
        return JSONObject().apply {
            put("model", model.modelName)
            put("messages", messages.map {
                JSONObject().apply {
                    put("role", it.role)
                    put("content", it.message)
                }
            })
            put("max_tokens", maxToken)
            put("temperature", temperature)
            put("stream", isStreamRequest)
        }
    }
}
