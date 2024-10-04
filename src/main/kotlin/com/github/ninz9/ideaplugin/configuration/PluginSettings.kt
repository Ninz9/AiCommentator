package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import org.jetbrains.annotations.NotNull


@State(
    name = "com.github.ninz9.ideaplugin.configuration.PluginState",
    storages = [Storage("PluginState.xml")]
)
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    class State {
        var currentModel: AiModel = AiModel.entries.first()
    }

    fun getInstance(): PluginSettings.State {
        return service<PluginSettings>().state
    }

    private var myState: State = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(@NotNull state: State) {
        myState = state
    }
}
