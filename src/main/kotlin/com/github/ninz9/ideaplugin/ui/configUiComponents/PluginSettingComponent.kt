package com.github.ninz9.ideaplugin.ui.configUiComponents

import com.github.ninz9.ideaplugin.llm.AiModel
import com.github.ninz9.ideaplugin.configuration.PluginSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import javax.swing.JComponent
import com.intellij.util.ui.FormBuilder


class PluginSettingComponent {

    private var currentVendorComboBox: ComboBox<AiModel> =
        ComboBox(AiModel.entries.toTypedArray()).apply {
            selectedItem = service<PluginSettings>().state.currentModel
        }

//    private var vendorSettingsLinks = mapOf(
//        "OpenAI" to OpenAIConfigurable::class.java as Configurable,
//        "Anthropic" to AnthropicConfigurable::class.java as  Configurable
//    ).entries.forEach { (vendor, cls) ->
//        ActionLink(vendor, {
//            val context = service<DataManager>().getDataContext(it.source as ActionLink)
//            val settings = context.getData(Settings.KEY)
//            settings?.select(cls)
//        })
//    }


    fun getSelectedVendor(): AiModel {
        return currentVendorComboBox.selectedItem as AiModel
    }

    fun setSelection(vendor: AiModel) {
        currentVendorComboBox.selectedItem = vendor
    }

    fun getComponent(): JComponent {
        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Current model", currentVendorComboBox)
            .panel
    }

}

