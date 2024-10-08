package com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class AnthropicConfigurable : Configurable {

    private val uiComponent = AnthropicUi()

    override fun getDisplayName(): String {
        return "Anthropic AI"
    }

    override fun isModified(): Boolean {
        return this.uiComponent.currentState() != service<AnthropicSetting>().state
                || this.uiComponent.token() != service<AnthropicSetting>().getApiToken()
    }

    override fun createComponent(): JComponent? {
        return uiComponent.getPanel()
    }

    override fun apply() {
        val currentState = uiComponent.currentState()
        val token = uiComponent.token()

        if (currentState.model != service<AnthropicSetting>().state.model)
            service<AnthropicSetting>().loadState(currentState)

        if (token != service<AnthropicSetting>().getApiToken())
            service<AnthropicSetting>().setApiToken(token)

    }
}
