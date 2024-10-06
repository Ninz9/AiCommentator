package com.github.ninz9.ideaplugin.utils

import com.google.gson.Gson
import com.intellij.openapi.components.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
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
        val call = client.newCall(request)

        return callbackFlow {
            call.enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    close(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val source = response.body?.source()
                    if (source != null) {
                        val buffer = source.buffer
                        val json = buffer.readUtf8()
                        val obj = Gson().fromJson(json, responseType)
                        trySend(obj)
                    }
                }
            })
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

        when(response.code) {
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

    private fun buildRequest(url: String, jsonBody: JSONObject, headers: Map<String, String> = emptyMap()): Request {
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
