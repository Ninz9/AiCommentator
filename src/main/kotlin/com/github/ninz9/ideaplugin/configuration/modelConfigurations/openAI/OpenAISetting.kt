package com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI

import com.github.ninz9.ideaplugin.configuration.SecureTokenStorage
import com.github.ninz9.ideaplugin.llm.AiModel
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.AvailableOpenAIModels
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service

/**
 * This class represents the settings for the OpenAI configuration.
 *
 * It implements the `PersistentStateComponent` interface, allowing it to persist its state.
 * The state includes the selected OpenAI model, temperature settings, maximum tokens, and token status.
 *
 */
@State(
    name = "OpenAIState",
    storages = [Storage("openAIState.xml")]
)
class OpenAISetting: PersistentStateComponent<OpenAISetting.State> {

    class State {
        var model: AvailableOpenAIModels = AvailableOpenAIModels.entries.first()
        var temperature: Double = 0.7
        var maxTokens: Int = 1024
        var isTokenSet: Boolean = false

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (model != other.model) return false
            if (temperature != other.temperature) return false
            if (maxTokens != other.maxTokens) return false
            return true
        }

        override fun hashCode(): Int {
            var result = model.hashCode()
            result = 31 * result + temperature.hashCode()
            result = 31 * result + maxTokens
            return result
        }
    }

    private var state = State()

    override fun getState(): OpenAISetting.State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }

    suspend fun getApiToken(): String {
        val apiKey = service<SecureTokenStorage>().getToken(AiModel.OpenAI)
        return apiKey
    }

    fun saveApiToken(token: String) {
        this.state.isTokenSet = true
        service<SecureTokenStorage>().setToken(AiModel.OpenAI, token)
    }
}
