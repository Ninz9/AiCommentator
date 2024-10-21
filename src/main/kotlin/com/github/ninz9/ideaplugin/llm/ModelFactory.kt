package com.github.ninz9.ideaplugin.llm

import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.OpenAiClient
import com.github.ninz9.ideaplugin.configuration.PluginSettings
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.AnthropicClient
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

/**
 * A factory class responsible for creating instances of different AI models based on the current plugin settings.
 *
 * This class provides methods to build clients for supported AI models like OpenAI and Anthropic,
 * using the configurations stored in their respective settings.
 */
@Service
class ModelFactory {
    fun getModel(): LLMClient {
        val aiModel = service<PluginSettings>().state.currentModel
        return when (aiModel) {
            AiModel.OpenAI -> service<OpenAiClient>()
            AiModel.Anthropic -> service<AnthropicClient>()
        }
    }
}
