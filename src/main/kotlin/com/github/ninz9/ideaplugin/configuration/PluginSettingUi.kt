package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import javax.swing.JComponent
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JPanel


class PluginSettingUi {

    private var currentVendorComboBox: ComboBox<AiModel> =
        ComboBox(AiModel.entries.toTypedArray()).apply {
            selectedItem = service<PluginSettings>().state.currentModel
        }

//    private var vendorSettingsLinks = mapOf(
//        "OpenAI" to OpenAIConfigurable::class.java ,
//        "Anthropic" to AnthropicConfigurable::class.java
//    ).entries.forEach { (vendor, cls) ->
//        ActionLink(vendor, {
//            val context = service<DataManager>().getDataContext(it.source as ActionLink)
//            val settings = context.getData(Settings.KEY)
//            settings?.select(cls)
//        })
//    }

    private val panel = FormBuilder.createFormBuilder()
        .addLabeledComponent("Current model", currentVendorComboBox)
        .panel

    private val mainPanel = JPanel(BorderLayout())

    init {
        mainPanel.add(panel, BorderLayout.NORTH)
    }

    fun getSelectedVendor(): AiModel {
        return currentVendorComboBox.selectedItem as AiModel
    }

    fun setSelection(vendor: AiModel) {
        currentVendorComboBox.selectedItem = vendor
    }

    fun getComponent(): JComponent {
        return mainPanel
    }
}

