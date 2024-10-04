package com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class OpenAIConfigurable: Configurable {

    private val openUiComponent = OpenAIUi()

    override fun getDisplayName(): String? {
        return "OpenAISettings"
    }

    override fun isModified(): Boolean {
        return this.openUiComponent.currentState != service<OpenAISetting>().openAiState
                || this.openUiComponent.token != service<OpenAISetting>().getApiToken()
        return false
    }

    override fun createComponent(): JComponent? {
        return openUiComponent.getComponent()
    }

    override fun apply() {
        service<OpenAISetting>().loadState(openUiComponent.currentState)
        service<OpenAISetting>().saveApiToken(openUiComponent.token)
    }
}

