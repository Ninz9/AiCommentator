package com.github.ninz9.aicommentator.llm.modelsImpl.openAI

import com.github.ninz9.aicommentator.configuration.modelConfigurations.openAI.OpenAISetting
import com.github.ninz9.aicommentator.llm.AiModel
import com.github.ninz9.aicommentator.llm.LLMClient
import com.github.ninz9.aicommentator.utils.types.ModelMessage
import com.github.ninz9.aicommentator.llm.modelsImpl.openAI.data.error.OpenAiErrorResponse
import com.github.ninz9.aicommentator.llm.modelsImpl.openAI.data.post.OpenAIResponse
import com.github.ninz9.aicommentator.llm.modelsImpl.openAI.data.stream.StreamOpenAiResponse
import com.github.ninz9.aicommentator.utils.ApiResponse
import com.github.ninz9.aicommentator.utils.HttpRequestHelper
import com.github.ninz9.aicommentator.utils.exeptions.AiCommentatorException
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

/**
 * Client implementation for interacting with OpenAI's API using various models.
 */
@Service()
class OpenAiClient() : LLMClient {
    val url = "https://api.openai.com/v1/chat/completions"

    override suspend fun sendRequestStream(messages: Collection<ModelMessage>): Flow<String> {

        val token = service<OpenAISetting>().getApiToken()
        val authHeader = "Bearer $token"

        val requestBody = buildJsonRequestBody(messages, true)

        return service<HttpRequestHelper>().stream(
            url,
            requestBody,
            mapOf("Authorization" to authHeader),
            StreamOpenAiResponse::class.java,
            OpenAiErrorResponse::class.java
        ).map {
            when (it) {
                is ApiResponse.Success -> it.data.choices.first().delta.content
                is ApiResponse.Error -> throw exceptionBuilder(it.error.error.code, it.error.error.message)
            }
        }
    }

    override suspend fun sendRequest(messages: Collection<ModelMessage>): String {

         val token = service<OpenAISetting>().getApiToken()
        val authHeader = "Bearer $token"

        val requestBody = buildJsonRequestBody(messages, false)

        val response = service<HttpRequestHelper>().post(
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
                    throw AiCommentatorException.EmptyMessage(AiModel.OpenAI)
                }
                return message
            }
            is ApiResponse.Error -> throw exceptionBuilder(response.error.error.code, response.error.error.message)
        }
    }

    private fun buildJsonRequestBody(messages: Collection<ModelMessage>, isStreamRequest: Boolean): JSONObject {
        return JSONObject().apply {
            put("model", service<OpenAISetting>().state.model.modelName)
            put("messages", messages.map {
                JSONObject().apply {
                    put("role", it.role)
                    put("content", it.message)
                }
            })
            put("max_tokens", service<OpenAISetting>().state.maxTokens)
            put("temperature", service<OpenAISetting>().state.temperature)
            put("stream", isStreamRequest)
        }
    }

    private fun exceptionBuilder(errorType: String, errorMessage: String = ""): Exception {
        return when (errorType) {
            "invalid_api_key" -> AiCommentatorException.InvalidToken(AiModel.OpenAI)
            "rate_limit_reached" -> AiCommentatorException.RateLimitError(AiModel.OpenAI)
            "insufficient_quota" -> AiCommentatorException.InsufficientQuota(AiModel.OpenAI)
            "server_error" -> AiCommentatorException.ServerError(AiModel.OpenAI)
            "service_unavailable" -> AiCommentatorException.ServiceUnavailable(AiModel.OpenAI)
            else -> AiCommentatorException.UnknownError(AiModel.OpenAI, errorMessage)
        }
    }
}
