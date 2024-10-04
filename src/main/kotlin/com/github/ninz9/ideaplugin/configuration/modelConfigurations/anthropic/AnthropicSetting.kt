package com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "AnthropicState",
    storages = [Storage("anthropicState.xml")])
class AnthropicSetting: PersistentStateComponent<AnthropicSetting.State> {

    class State {
        var tokens: Int = 1024
        var temperature: Float = 0.5f
    }

    private var state = State()

    override fun getState(): State? {
        return state
    }

    override fun loadState(p0: State) {
        state = p0
    }
}
