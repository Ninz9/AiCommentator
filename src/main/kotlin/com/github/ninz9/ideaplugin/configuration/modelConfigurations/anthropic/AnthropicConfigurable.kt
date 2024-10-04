package com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import javax.swing.JComponent

class AnthropicConfigurable: Configurable {

    val uiComponent = AnthropicUi()

    override fun getDisplayName(): String? {
        return "Anthropic AI"
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun createComponent(): JComponent? {
        return uiComponent.getPanel()
    }

    override fun apply() {
        println("boba")
    }
}
