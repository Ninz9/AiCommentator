package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic

import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class AnthropicClientTest: BasePlatformTestCase() {

    private lateinit var anthropicClient: AnthropicClient
    private lateinit var apiToken: String

    override fun setUp() {
        super.setUp()

        apiToken = System.getenv("ANTHROPIC_API_KEY") ?: ""
        anthropicClient = AnthropicClient()
    }

    fun testSendRequest() = runBlocking {
        val messages = listOf(ModelMessage("user", "Hello, how are you?"))

        val response = anthropicClient.sendRequest(messages)

        assertNotNull(response)
        assertTrue(response.isNotEmpty())
        println("Response: $response")
    }

    fun testSendRequestWithInvalidToken() = runBlocking {
        val invalidClient = AnthropicClient()
        val messages = listOf(ModelMessage("user", "Hello"))

       assertThrows(Exception::class.java) {
            runBlocking {
                invalidClient.sendRequest(messages)
            }
        }
    }


    fun testSendRequestStream() = runBlocking {
        val messages = listOf(ModelMessage("user", "Count from 1 to 5"))

        val responseFlow = anthropicClient.sendRequestStream(messages)
        val responses = responseFlow.toList()

        assertFalse(responses.isEmpty())
        responses.forEach { println(it) }
    }
}