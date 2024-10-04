package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI

import com.github.ninz9.ideaplugin.llm.LLMClient
import com.github.ninz9.ideaplugin.generators.ModelMessage
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.post.OpenAIResponse
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.data.stream.StreamOpenAiResponse
import com.github.ninz9.ideaplugin.utils.HttpRequestHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
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

        if (token.isEmpty()) {
            throw Exception("API token not found")
        }


        //val openAi: OpenAI  = OpenAI(token, timeout = Timeout(socket = 10.seconds))
        val requestBody = buildJsonRequestBody(messages, true)

//        return openAi.chatCompletions(requestBody).map {
//            it.choices.first().delta?.content ?: ""
//        }.flowOn(Dispatchers.IO)

            return HttpRequestHelper().stream(
                url,
                requestBody,
                mapOf("Authorization" to authHeader),
                StreamOpenAiResponse::class.java
            ).map {
                it.choices.first().delta.content
            }.flowOn(Dispatchers.IO).buffer()
    }

    override suspend fun sendRequest(messages: Collection<ModelMessage>): String {

        val requestBody = buildJsonRequestBody(messages, false)

        if (token.isEmpty()) {
            throw Exception("API token not found")
        }

//        val openAI = OpenAI(token, timeout = Timeout(socket = 10.seconds))
//

        val response = HttpRequestHelper().post(
            url,
            requestBody,
            mapOf("Authorization" to authHeader),
            OpenAIResponse::class.java
        )

        return response.choices.first().message.content
//        return openAI.chatCompletion(buildOpenAILibraryBody(messages)).choices.first().message.content ?: ""
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

//    private fun buildOpenAILibraryBody(messages: Collection<ModelMessage>): ChatCompletionRequest {
//        return ChatCompletionRequest(
//            model = ModelId(model.modelName),
//            messages = messages.map {
//                ChatMessage (
//                    role = roleAdapter(it.role),
//                    content = it.message
//                )
//            },
//            maxTokens = maxToken,
//            temperature = temperature
//        )
//    }
//
//    private fun roleAdapter(role: String): ChatRole{
//        return when (role) {
//            "assistant" -> ChatRole.Assistant
//            "user" -> ChatRole.User
//            else -> ChatRole.User
//        }
//    }
}
