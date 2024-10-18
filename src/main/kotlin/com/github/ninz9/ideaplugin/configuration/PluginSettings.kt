package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.NotNull


/**
 * A service class that handles the persistent state of plugin settings.
 *
 * This class is responsible for saving and loading the state of plugin settings,
 * allowing the application to maintain user preferences across sessions.
 *
 * The state is stored in an XML file named `PluginState.xml`.
 */
@State(
    name = "com.github.ninz9.ideaplugin.configuration.PluginState",
    storages = [Storage("PluginState.xml")]
)
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    class State {
        var currentModel: AiModel = AiModel.entries.first()
    }

    private var myState: State = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(@NotNull state: State) {
        myState = state
    }
}
