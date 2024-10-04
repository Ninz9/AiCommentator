package com.github.ninz9.ideaplugin.llm

import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.OpenAiClient
import com.github.ninz9.ideaplugin.configuration.PluginSettings
import com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI.OpenAISetting
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class ModelFactoryWithSimplePromptGenerator {


    fun getModel(): LLMClient {
        val aiModel = service<PluginSettings>().state.currentModel
        return when (aiModel) {
            AiModel.OpenAI-> this.buildOpenAIModel()
            else -> throw IllegalArgumentException("Unsupported model")
        }
    }

    fun buildOpenAIModel(): OpenAiClient {
        val token = service<OpenAISetting>().getApiToken() ?: throw IllegalArgumentException("No token found")
        val modelState = service<OpenAISetting>().openAiState
        return OpenAiClient(token, modelState.currentModel, modelState.maxTokens, modelState.temperature)
    }
}
