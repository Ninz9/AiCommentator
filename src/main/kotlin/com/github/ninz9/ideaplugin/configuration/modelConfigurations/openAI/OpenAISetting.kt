package com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI

import com.github.ninz9.ideaplugin.configuration.SecureTokenStorage
import com.github.ninz9.ideaplugin.llm.AiModel
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.AvailableOpenAIModels
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service

@State(
    name = "OpenAIState",
    storages = [Storage("openAIState.xml")]
)
class OpenAISetting: PersistentStateComponent<OpenAISetting.State> {

    class State {
        var currentModel: AvailableOpenAIModels = AvailableOpenAIModels.entries.first()
        var temperature: Double = 0.7
        var maxTokens: Int = 1024

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (currentModel != other.currentModel) return false
            if (temperature != other.temperature) return false
            if (maxTokens != other.maxTokens) return false

            return true
        }
    }

    var openAiState = State()

    override fun getState(): OpenAISetting.State? {
        return openAiState
    }

    override fun loadState(state: State) {
        this.openAiState = state
    }

    fun getApiToken(): String? {
        val apiKey = service<SecureTokenStorage>().getTokens(AiModel.OpenAI)
        println("token $apiKey")
        return apiKey
    }

    fun saveApiToken(token: String) {
        service<SecureTokenStorage>().setToken(AiModel.OpenAI, token)
    }
}
