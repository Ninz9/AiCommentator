package com.github.ninz9.aicommentator.llm.modelsImpl

import com.github.ninz9.aicommentator.configuration.PluginSettings
import com.github.ninz9.aicommentator.llm.AiModel
import com.github.ninz9.aicommentator.llm.ModelFactory
import com.github.ninz9.aicommentator.llm.modelsImpl.anthropic.AnthropicClient
import com.github.ninz9.aicommentator.llm.modelsImpl.openAI.OpenAiClient
import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.replaceService
import kotlinx.coroutines.runBlocking

class ModelFactoryTest: BasePlatformTestCase() {

     lateinit var modelFactory: ModelFactory

    override fun setUp() {
        super.setUp()
        val pluginSettings = PluginSettings()

        ApplicationManager.getApplication().replaceService(
            PluginSettings::class.java,
            pluginSettings,
            testRootDisposable
        )

        modelFactory = ModelFactory()
    }

    fun testGetOpenAIModel() {
        ApplicationManager.getApplication().getService(PluginSettings::class.java).state.currentModel = AiModel.OpenAI

        runBlocking {
            val model = modelFactory.getModel()
            assertNotNull(model)
            assertTrue(model is OpenAiClient)
        }
    }

    fun testGetAnthropicModel() {

        ApplicationManager.getApplication().getService(PluginSettings::class.java).state.currentModel = AiModel.Anthropic

        runBlocking {
            val model = modelFactory.getModel()
            assertNotNull(model)
            assertTrue(model is AnthropicClient)
        }
    }
}