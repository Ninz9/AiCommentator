package com.github.ninz9.ideaplugin.llm

import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.OpenAiClient
import com.github.ninz9.ideaplugin.configuration.PluginSettings
import com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic.AnthropicSetting
import com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI.OpenAISetting
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.AnthropicClient
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class ModelFactory {


    fun getModel(): LLMClient {
        val aiModel = service<PluginSettings>().state.currentModel
        return when (aiModel) {
            AiModel.OpenAI-> this.buildOpenAIModel()
            AiModel.Anthropic -> this.buildAnthropicModel()
            else -> throw IllegalArgumentException("Unsupported model")
        }
    }

    fun buildOpenAIModel(): OpenAiClient {
        val token = service<OpenAISetting>().getApiToken() ?: throw IllegalArgumentException("No token found")
        val modelState = service<OpenAISetting>().openAiState
        return OpenAiClient(token, modelState.currentModel, modelState.maxTokens, modelState.temperature)
    }

    fun buildAnthropicModel(): AnthropicClient {
        val token = service<AnthropicSetting>().getApiToken() ?: throw IllegalArgumentException("No token found")
        val modelState = service<AnthropicSetting>().state
        return AnthropicClient(token, modelState.model, modelState.maxTokens, modelState.temperature)
    }
}
