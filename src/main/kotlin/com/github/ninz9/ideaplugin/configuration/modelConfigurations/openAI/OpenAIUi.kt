package com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI

import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.AvailableOpenAIModels
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel
import javax.swing.JTextField


class OpenAIUi {
    private val tokenField: JBPasswordField = JBPasswordField()
    private val currentModelComboBox: ComboBox<AvailableOpenAIModels> =
        ComboBox<AvailableOpenAIModels>(EnumComboBoxModel(AvailableOpenAIModels::class.java)).apply {
            selectedItem = service<OpenAISetting>().openAiState.currentModel
        }
    private val temperatureField: JTextField = JTextField(service<OpenAISetting>().openAiState.temperature.toString())

    private val maxTokensField: JTextField = JTextField(service<OpenAISetting>().openAiState.maxTokens.toString())

    init {
        tokenField.text = service<OpenAISetting>().getApiToken()
    }

    private val panel: JPanel =

        FormBuilder.createFormBuilder()
            .addLabeledComponent("API token", tokenField)
            .addVerticalGap(10).addSeparator().addVerticalGap(10)
            .addLabeledComponent("Current model", currentModelComboBox)
            .addVerticalGap(10).addSeparator().addVerticalGap(10)
            .addLabeledComponent("Temperature", temperatureField)
            .addLabeledComponent("Max tokens", maxTokensField)
            .panel


    fun getComponent(): JPanel {
        return panel
    }

    var token
        set(value) {
            tokenField.text = value
        }
        get() = String(tokenField.password)

    var currentModel : AvailableOpenAIModels
        set(value) {
            currentModelComboBox.selectedItem = value
        }
        get() = currentModelComboBox.selectedItem as AvailableOpenAIModels

    var temperature: Double
        set(value) {
            temperatureField.text = value.toString()
        }
        get() = temperatureField.text.toDouble()

    var maxTokens
        set(value) {
            maxTokensField.text = value.toString()
        }
        get() = maxTokensField.text.toInt()

    var currentState: OpenAISetting.State
        get() = OpenAISetting.State().apply {
            currentModel = currentModelComboBox.selectedItem as AvailableOpenAIModels
            temperature = temperatureField.text.toDouble()
            maxTokens = maxTokensField.text.toInt()
        }
        set(value) {
            currentModel = value.currentModel
            temperature = value.temperature
            maxTokens = value.maxTokens
        }

}
