package com.github.ninz9.ideaplugin.llm.modelsImpl.openAI

import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class OpenAIClientTest: BasePlatformTestCase() {

    private lateinit var openAiClient: OpenAiClient
    private lateinit var apiToken: String

    override fun setUp() {
        super.setUp()

        // load api token from System environment
        apiToken = System.getenv("OPENAI_API_KEY") ?: ""
        openAiClient = OpenAiClient(
            token = apiToken,
            model = AvailableOpenAIModels.Gpt4oMini,
            maxToken = 100,
            temperature = 0.7
        )
    }

    fun testSendRequest() = runBlocking {
        val messages = listOf(ModelMessage("user", "Hello, how are you?"))

        val response = openAiClient.sendRequest(messages)

        assertNotNull(response)
        assertTrue(response.isNotEmpty())
        println("Response: $response")
    }

    fun testSendRequestWithInvalidToken() {
        val invalidClient = OpenAiClient(
            token = "invalid_token",
            model = AvailableOpenAIModels.Gpt4oMini,
            maxToken = 100,
            temperature = 0.7
        )
        val messages = listOf(ModelMessage("user", "Hello"))

        assertThrows(Exception::class.java) {
            runBlocking {
                invalidClient.sendRequest(messages)
            }
        }
    }

    fun testSendRequestStream() = runBlocking {
        val messages = listOf(ModelMessage("user", "Count from 1 to 5"))

        val responseFlow = openAiClient.sendRequestStream(messages)
        val responses = responseFlow.toList()

        assertFalse(responses.isEmpty())
        responses.forEach { println(it) }
    }

    fun testSendRequestWithDifferentRoles() = runBlocking {
        val messages = listOf(
            ModelMessage("system", "You are a helpful assistant."),
            ModelMessage("user", "What's the capital of France?"),
            ModelMessage("assistant", "The capital of France is Paris."),
            ModelMessage("user", "What's its population?")
        )

        val response = openAiClient.sendRequest(messages)

        assertNotNull(response)
        assertTrue(response.isNotEmpty())
        println("Response: $response")
    }

    override fun getTestDataPath(): String {
        return "testData"
    }
}