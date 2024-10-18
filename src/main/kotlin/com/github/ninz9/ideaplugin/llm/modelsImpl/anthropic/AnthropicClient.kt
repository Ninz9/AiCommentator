package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic

import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.github.ninz9.ideaplugin.llm.LLMClient
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.error.AnthropicErrorResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.post.AnthropicResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.stream.AnthropicStreamResponse
import com.github.ninz9.ideaplugin.utils.ApiResponse
import com.github.ninz9.ideaplugin.utils.HttpRequestHelper
import com.github.ninz9.ideaplugin.utils.exeptions.ErrorType
import com.github.ninz9.ideaplugin.utils.exeptions.clientExeptions.AnthropicClientException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.json.JSONObject

/**
 * A client for interacting with Anthropic's API for Language Learning Models (LLM).
 *
 * @property token The API token used for authentication.
 * @property model The specific model to be used for the requests.
 * @property maxTokens The maximum number of tokens to generate.
 * @property temperature The temperature parameter controls randomness in the response.
 */
class AnthropicClient(
    private val token: String,
    private val model: AvailableAnthropicModels,
    private val maxTokens: Int,
    private val temperature: Double
) : LLMClient {


    private val url = "https://api.anthropic.com/v1/messages"

    override suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String> {
        val requestBody = buildJsonRequestBody(messageAdapter(messages), true)
        val headers = mapOf(
            "anthropic-version" to "2023-06-01",
            "x-api-key" to token,
            "content-Type" to "application/json"
        )

        val response = HttpRequestHelper().stream(
            url,
            requestBody,
            headers,
            AnthropicStreamResponse::class.java,
            AnthropicErrorResponse::class.java
        )

        return response
            .map {
                when (it) {
                    is ApiResponse.Success -> it
                    is ApiResponse.Error -> throw exceptionBuilder(it.error.error.type, it.error.error.message)
                }
            }
            .filter {
                it.data.type == "content_block_delta" || it.data.type == "content_block_start"
            }.map {
                it.data?.delta?.text ?: ""
            }
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

        when (response) {
            is ApiResponse.Success -> {
                val message = response.data.content
                if (message.isEmpty()) {
                    throw AnthropicClientException(ErrorType.EMPTY_MESSAGE)
                }
                return response.data.content.first().text
            }
            is ApiResponse.Error ->  throw exceptionBuilder(response.error.error.type, response.error.error.message)
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

    private fun messageAdapter(messages: Collection<ModelMessage>): Map<String, Collection<String>> {
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

    private fun exceptionBuilder(errorType: String, errorMessage: String): Exception {
        return when (errorType) {
            "authentication_error" ->  AnthropicClientException(ErrorType.INVALID_TOKEN)
            "permissions_error" ->  AnthropicClientException(ErrorType.PERMISSION_DENIED)
            "request_too_large" -> AnthropicClientException(ErrorType.REQUEST_TOO_LARGE)
            "rate_limit_error" -> AnthropicClientException(ErrorType.RATE_LIMIT_ERROR)
            "api_error" -> AnthropicClientException(ErrorType.SERVER_ERROR)
            "overloaded_error" -> AnthropicClientException(ErrorType.OVERLOADED_ERROR)
            else -> AnthropicClientException(ErrorType.UNKNOWN_ERROR, errorMessage)
        }
    }
}
