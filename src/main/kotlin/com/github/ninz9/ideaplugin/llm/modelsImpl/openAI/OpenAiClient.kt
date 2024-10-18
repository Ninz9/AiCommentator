package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI

import com.github.ninz9.ideaplugin.llm.LLMClient
import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.error.OpenAiErrorResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.post.OpenAIResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.stream.StreamOpenAiResponse
import com.github.ninz9.ideaplugin.utils.ApiResponse
import com.github.ninz9.ideaplugin.utils.HttpRequestHelper
import com.github.ninz9.ideaplugin.utils.exeptions.ErrorType
import com.github.ninz9.ideaplugin.utils.exeptions.clientExeptions.OpenAIClientException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

/**
 * Client implementation for interacting with OpenAI's API using various models.
 *
 * @property token The authentication token for accessing OpenAI API.
 * @property model The specific OpenAI model to be utilized for the requests.
 * @property maxToken The maximum number of tokens to be used in the response.
 * @property temperature The temperature setting affects the randomness of responses.
 */
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
                is ApiResponse.Error -> throw exceptionBuilder(it.error.error.code, it.error.error.message)
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
            is ApiResponse.Success -> {
                val message = response.data.choices.first().message.content
                if (message.isEmpty()) {
                    throw OpenAIClientException(ErrorType.EMPTY_MESSAGE)
                }
                return message
            }
            is ApiResponse.Error -> throw exceptionBuilder(response.error.error.code, response.error.error.message)
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

    private fun exceptionBuilder(errorType: String, errorMessage: String = ""): Exception {
        return when (errorType) {
            "invalid_api_key" -> OpenAIClientException(ErrorType.INVALID_TOKEN)
            "rate_limit_reached" -> OpenAIClientException(ErrorType.RATE_LIMIT_ERROR)
            "insufficient_quota" -> OpenAIClientException(ErrorType.INSUFFICIENT_QUOTA)
            "server_error" -> OpenAIClientException(ErrorType.SERVER_ERROR)
            "service_unavailable" -> OpenAIClientException(ErrorType.SERVICE_UNAVAILABLE)
            else -> OpenAIClientException(ErrorType.UNKNOWN_ERROR, errorMessage)
        }
    }
}
