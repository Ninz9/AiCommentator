package com.github.ninz9.ideaplugin.utils

import com.github.ninz9.ideaplugin.configuration.PluginSettings
import com.github.ninz9.ideaplugin.utils.exeptions.AiCommentatorException
import com.github.ninz9.ideaplugin.utils.exeptions.ErrorType
import com.google.gson.Gson
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.BufferedReader
import java.net.SocketTimeoutException


/**
 * A helper class for making HTTP requests using OkHttp library.
 */
@Service()
class HttpRequestHelper() {

    private val client = OkHttpClient()

    /**
     * Streams data from a specified URL with a given JSON body and headers.
     *
     * @param url The URL to make the request to.
     * @param jsonBody The JSON body to be sent with the request.
     * @param headers Optional headers to include in the request.
     * @param responseType The class to be used for successful response body deserialization.
     * @param errorType The class to be used for error response body deserialization.
     * @return A Flow emitting ApiResponse instances, representing either success or error.
     */
    fun <T, E> stream(
        url: String,
        jsonBody: JSONObject,
        headers: Map<String, String> = emptyMap(),
        responseType: Class<T>,
        errorType: Class<E>
    ): Flow<ApiResponse<T, E>> {

        val request = buildRequest(url, jsonBody, headers)
        val tmp = client.newCall(request)

        return flow {
            val response = tmp.execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                val error = Gson().fromJson(errorBody, errorType)
                emit(ApiResponse.Error(error))
                return@flow
            }

            response.body?.use { responseBody ->
                val source = responseBody.source()
                val reader = BufferedReader(source.inputStream().reader())
                try {
                    while (!source.exhausted()) {
                        val line = reader.readLine() ?: break
                        if (line.startsWith("data:") && line != "data: [DONE]") {
                            val data = line.substringAfter("data: ")
                            val parsedData = Gson().fromJson(data, responseType)
                            emit(ApiResponse.Success(parsedData))
                        }
                    }
                } finally {
                    reader.close()
                }
            }
        }
    }

    /**
     * Executes a POST request to the given URL with the provided JSON body and headers, and deserializes the response.
     *
     * @param url The URL to make the request to.
     * @param jsonBody The JSON body to be sent with the request.
     * @param headers Optional headers to include in the request. Defaults to an empty map.
     * @param responseType The class to be used for successful response body deserialization.
     * @param errorType The class to be used for error response body deserialization.
     * @return An instance of ApiResponse representing either a successful response or an error.
     */
    fun <T, E> post(
        url: String,
        jsonBody: JSONObject,
        headers: Map<String, String> = emptyMap(),
        responseType: Class<T>,
        errorType: Class<E>
    ): ApiResponse<T, E> {
        val request = buildRequest(url, jsonBody, headers)
        val response: okhttp3.Response
        try {
            response = client.newCall(request).execute()
        } catch (e: SocketTimeoutException) {
            throw AiCommentatorException(ErrorType.TIMEOUT_ERROR, service<PluginSettings>().state.currentModel)
        }

        val body = response.body?.string()

        when (response.code) {
            in 200..299 -> {
                val data = Gson().fromJson(body, responseType)
                return ApiResponse.Success(data)
            }
            in 400..499 -> {
                val error = Gson().fromJson(body, errorType)
                return ApiResponse.Error(error)
            }
            else -> {
                throw Exception("Unexpected response code: ${response.code}")
            }
        }
    }

    private fun buildRequest(
        url: String,
        jsonBody: JSONObject,
        headers: Map<String, String> = emptyMap()
    ): Request {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = jsonBody.toString().toRequestBody(mediaType)
        val requestBuilder = Request.Builder()
            .url(url)
            .post(body)

        for ((key, value) in headers) {
            requestBuilder.addHeader(key, value)
        }

        return requestBuilder.build()
    }
}

sealed class ApiResponse<out T, out E> {
    data class Success<T>(val data: T) : ApiResponse<T, Nothing>()
    data class Error<E>(val error: E) : ApiResponse<Nothing, E>()
}
