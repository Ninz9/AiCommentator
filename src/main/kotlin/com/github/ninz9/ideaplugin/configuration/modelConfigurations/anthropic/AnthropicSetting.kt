package com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic

import com.github.ninz9.ideaplugin.configuration.SecureTokenStorage
import com.github.ninz9.ideaplugin.llm.AiModel
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.AvailableAnthropicModels
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(
    name = "AnthropicState",
    storages = [Storage("anthropicState.xml")])
class AnthropicSetting: PersistentStateComponent<AnthropicSetting.State> {

    class State {
        var model: AvailableAnthropicModels = AvailableAnthropicModels.haiku3
        var maxTokens: Int = 1024
        var temperature: Double = 0.5

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (model != other.model) return false
            if (maxTokens != other.maxTokens) return false
            if (temperature != other.temperature) return false

            return true
        }
    }

    private var state = State()

    override fun getState(): State {
        return state
    }

    override fun loadState(newState: State) {
        state = newState
    }

    fun getApiToken(): String {
        return service<SecureTokenStorage>().getTokens(AiModel.Anthropic)
    }

    fun setApiToken(token: String) {
        service<SecureTokenStorage>().setToken(AiModel.Anthropic, token)
    }
}
