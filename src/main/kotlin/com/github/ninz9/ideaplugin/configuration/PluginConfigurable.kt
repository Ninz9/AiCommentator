package com.github.ninz9.ideaplugin.configuration

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class PluginConfigurable: Configurable {

    private var settingUiComponent: PluginSettingUi = PluginSettingUi()

    override fun createComponent(): JComponent {
        return settingUiComponent.getComponent()
    }

    override fun isModified(): Boolean {
        val state: PluginSettings.State = service<PluginSettings>().state
        return state.currentModel != settingUiComponent.getSelectedVendor()
    }

    override fun apply() {
        val state: PluginSettings.State = service<PluginSettings>().state
        state.currentModel = settingUiComponent.getSelectedVendor()
        service<PluginSettings>().loadState(state)
    }

    override fun getDisplayName(): String {
        return "My Plugin Settings"
    }
}
