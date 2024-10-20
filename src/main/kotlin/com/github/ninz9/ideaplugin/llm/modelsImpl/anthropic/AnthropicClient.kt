package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic

import com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic.AnthropicSetting
import com.github.ninz9.ideaplugin.llm.AiModel
import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.github.ninz9.ideaplugin.llm.LLMClient
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.error.AnthropicErrorResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.post.AnthropicResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.data.stream.AnthropicStreamResponse
import com.github.ninz9.ideaplugin.utils.ApiResponse
import com.github.ninz9.ideaplugin.utils.HttpRequestHelper
import com.github.ninz9.ideaplugin.utils.exeptions.AiCommentatorException
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.json.JSONObject

/**
 * A client for interacting with Anthropic's API for Language Learning Models (LLM).
 */
@Service()
class AnthropicClient() : LLMClient {
    private val url = "https://api.anthropic.com/v1/messages"

    override suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String> {

        val token = service<AnthropicSetting>().getApiToken()

        val requestBody = buildJsonRequestBody(messageAdapter(messages), true)

        val headers = mapOf(
            "anthropic-version" to "2023-06-01",
            "x-api-key" to token,
            "content-Type" to "application/json"
        )

        val response = service<HttpRequestHelper>().stream(
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
                it.data.delta?.text ?: ""
            }
    }

    override suspend fun sendRequest(messages: Collection<ModelMessage>): String {

         val token = service<AnthropicSetting>().getApiToken()

        val requestBody = buildJsonRequestBody(messageAdapter(messages), false)

        val headers = mapOf(
            "anthropic-version" to "2023-06-01",
            "x-api-key" to token,
            "content-Type" to "application/json"
        )

        val response = service<HttpRequestHelper>().post(
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
                    throw AiCommentatorException.EmptyMessage(AiModel.Anthropic)
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
        json.put("model", service<AnthropicSetting>().state.model.modelName)
        json.put("max_tokens", service<AnthropicSetting>().state.maxTokens)
        json.put("temperature", service<AnthropicSetting>().state.temperature)
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
            "authentication_error" -> AiCommentatorException.InvalidToken(AiModel.Anthropic)
            "permissions_error" -> AiCommentatorException.PermissionDenied(AiModel.Anthropic)
            "request_too_large" -> AiCommentatorException.RequestTooLarge(AiModel.Anthropic)
            "rate_limit_error" -> AiCommentatorException.RateLimitError(AiModel.Anthropic)
            "api_error" -> AiCommentatorException.ServerError(AiModel.Anthropic)
            "overloaded_error" -> AiCommentatorException.OverloadedError(AiModel.Anthropic)
            else -> AiCommentatorException.UnknownError(AiModel.Anthropic, errorMessage)
        }
    }
}
