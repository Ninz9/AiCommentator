package com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI

import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.AvailableOpenAIModels
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextField


class OpenAIUi {
    private val tokenField: JBPasswordField = JBPasswordField()
    private val currentModelComboBox: ComboBox<AvailableOpenAIModels> =
        ComboBox(EnumComboBoxModel(AvailableOpenAIModels::class.java)).apply {
            selectedItem = service<OpenAISetting>().openAiState.currentModel
        }
    private val temperatureField: JTextField = JTextField(service<OpenAISetting>().openAiState.temperature.toString())

    private val maxTokensField: JTextField = JTextField(service<OpenAISetting>().openAiState.maxTokens.toString())

    private val mainPanel = JPanel(BorderLayout())

    private val panel: JPanel =

        FormBuilder.createFormBuilder()
            .addLabeledComponent("API token", tokenField)
            .addVerticalGap(10).addSeparator().addVerticalGap(10)
            .addLabeledComponent("Current model", currentModelComboBox)
            .addVerticalGap(10).addSeparator().addVerticalGap(10)
            .addLabeledComponent("Temperature", temperatureField)
            .addLabeledComponent("Max tokens", maxTokensField)
            .panel


    init {
        tokenField.text = service<OpenAISetting>().getApiToken()
        mainPanel.add(panel, BorderLayout.NORTH)
    }

    fun getComponent(): JPanel {
        return mainPanel
    }

    fun token(): String {
        return String(tokenField.password)
    }

    fun currentState(): OpenAISetting.State {
        return OpenAISetting.State().apply {
            currentModel = currentModelComboBox.selectedItem as AvailableOpenAIModels
            temperature = temperatureField.text.toDouble()
            maxTokens = maxTokensField.text.toInt()
        }
    }
}
