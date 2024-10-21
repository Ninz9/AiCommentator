package com.github.ninz9.aicommentator.configuration.modelConfigurations.anthropic

import com.github.ninz9.aicommentator.configuration.SecureTokenStorage
import com.github.ninz9.aicommentator.llm.AiModel
import com.github.ninz9.aicommentator.llm.modelsImpl.anthropic.AvailableAnthropicModels
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

/**
 * Class responsible for managing the persistent state of Anthropic settings.
 *
 * This class implements the PersistentStateComponent interface and manages its state
 * using the nested State class which holds the configuration for Anthropic models,
 * such as model type, maximum number of tokens, temperature, and token set status.
 *
 * The state is persisted in an XML file named anthropicState.xml.
 *
 * The class provides methods to get and load the state, along with functionality
 * to manage API tokens securely through a SecureTokenStorage service.
 */
@State(
    name = "AnthropicState",
    storages = [Storage("anthropicState.xml")])
class AnthropicSetting: PersistentStateComponent<AnthropicSetting.State> {

    class State {
        var model: AvailableAnthropicModels = AvailableAnthropicModels.entries.first()
        var maxTokens: Int = 1024
        var temperature: Double = 0.5
        var isTokenSet: Boolean = false

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (model != other.model) return false
            if (maxTokens != other.maxTokens) return false
            if (temperature != other.temperature) return false

            return true
        }

        override fun hashCode(): Int {
            var result = model.hashCode()
            result = 31 * result + maxTokens
            result = 31 * result + temperature.hashCode()
            return result
        }
    }

    private var state = State()

    override fun getState(): State {
        return state
    }

    override fun loadState(newState: State) {
        if (newState.isTokenSet) {
            state = newState
        } else {
            state = newState
            state.isTokenSet = true
        }
    }

    suspend fun getApiToken(): String {
        return service<SecureTokenStorage>().getToken(AiModel.Anthropic)
    }

    fun saveApiToken(token: String) {
        service<SecureTokenStorage>().setToken(AiModel.Anthropic, token)
    }
}
