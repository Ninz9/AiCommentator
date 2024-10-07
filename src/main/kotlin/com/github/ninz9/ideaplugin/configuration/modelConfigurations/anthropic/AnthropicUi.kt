package com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic

import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.AvailableAnthropicModels
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class AnthropicUi {

    private val tokenField: JBPasswordField = JBPasswordField()
    private val currentModelComboBox: ComboBox<AvailableAnthropicModels> =
        ComboBox(EnumComboBoxModel(AvailableAnthropicModels::class.java)).apply {
            selectedItem = service<AnthropicSetting>().state.model
        }
    private val temperatureField: JTextField = JTextField(service<AnthropicSetting>().state.temperature.toString())

    private val maxTokensField: JTextField = JTextField(service<AnthropicSetting>().state.maxTokens.toString())

    init {
        tokenField.text = service<AnthropicSetting>().getApiToken()
    }

    private val panel = FormBuilder.createFormBuilder()
        .addLabeledComponent("API token", tokenField)
        .addVerticalGap(10).addSeparator().addVerticalGap(10)
        .addLabeledComponent("Current model", currentModelComboBox)
        .addVerticalGap(10).addSeparator().addVerticalGap(10)
        .addLabeledComponent("Temperature", temperatureField)
        .addLabeledComponent("Max tokens", maxTokensField)
        .panel

    private val mainPanel = JPanel(BorderLayout())

    init {
        mainPanel.add(panel, BorderLayout.NORTH)
    }

    fun getPanel(): JComponent {
        return mainPanel
    }

    fun token(): String {
        return String(tokenField.password)
    }

    fun currentState(): AnthropicSetting.State {
        return AnthropicSetting.State().apply {
            model = currentModelComboBox.selectedItem as AvailableAnthropicModels
            temperature = temperatureField.text.toDouble()
            maxTokens = maxTokensField.text.toInt()
        }
    }
}
