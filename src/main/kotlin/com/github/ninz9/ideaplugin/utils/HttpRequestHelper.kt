package com.github.ninz9.ideaplugin.utils

import com.google.gson.Gson
import com.intellij.openapi.components.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.BufferedReader
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

@Service()
class HttpRequestHelper() {

    private val client = OkHttpClient()

    fun <T> stream(
        url: String,
        jsonBody: JSONObject,
        headers: Map<String, String> = emptyMap(),
        responseType: Class<T>
    ): Flow<T> {

        val request = buildRequest(url, jsonBody, headers)
        val tmp = client.newCall(request)

        return flow {

            val response = tmp.execute()

            if (!response.isSuccessful) throw Exception("Unexpected response code: ${response.code}")

            response.body?.use { responseBody ->
                val source = responseBody.source()
                val reader = BufferedReader(source.inputStream().reader())
                try {
                    while (!source.exhausted()) {
                        val line = reader.readLine() ?: break

                        if (line.startsWith("data:") && line != "data: [DONE]") {
                            val data = line.substringAfter("data: ")
                            val parsedData = Gson().fromJson(data, responseType)
                            emit(parsedData)
                        }
                    }
                } catch (e: Exception) {

                    println("Error while reading stream: ${e.message}")
                } finally {
                    reader.close()
                }
            }
        }


    }

    fun <T, E> post(
        url: String,
        jsonBody: JSONObject,
        headers: Map<String, String> = emptyMap(),
        responseType: Class<T>,
        errorType: Class<E>
    ): ApiResponse<T, E> {
        val request = buildRequest(url, jsonBody, headers)

        val response = client.newCall(request).execute()
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
