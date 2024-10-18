package com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.github.ninz9.ideaplugin.configuration.LLMSettingsPanel
import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.AvailableAnthropicModels
import com.github.ninz9.ideaplugin.utils.emptyText
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toMutableProperty
import com.intellij.ui.dsl.builder.toNullableProperty


/**
 * The AnthropicConfigurable class represents a configurable settings panel for Anthropic AI models.
 * It extends the BoundConfigurable base class and implements the LLMSettingsPanel interface.
 * This class provides a graphical user interface (GUI) for users to configure model selections,
 * API tokens, temperature, and maximum number of tokens.
 */
class AnthropicConfigurable : BoundConfigurable("Anthropic AI"), LLMSettingsPanel {

    private val tokenField: JBPasswordField = JBPasswordField()

    private val currentModelComboBox: ComboBox<AvailableAnthropicModels> =
        ComboBox(EnumComboBoxModel(AvailableAnthropicModels::class.java))


    override fun createPanel(): DialogPanel {
        return panel {
            vendorRow()
            tokenRow()
            temperatureRow(service<AnthropicSetting>().state::temperature.toMutableProperty())
            maxTokensField(service<AnthropicSetting>().state::maxTokens.toMutableProperty())
        }
    }

    fun Panel.vendorRow() {
        row {
            label(AiCommentatorBundle.message("settings.selected_model")).widthGroup("labels")
            cell(currentModelComboBox)
                .columns(30)
                .applyToComponent {
                    isEditable = true
                }.bindItem(service<AnthropicSetting>().state::model.toNullableProperty())
        }
    }

    fun Panel.tokenRow() {
        row {
            label(AiCommentatorBundle.message("settings.token")).widthGroup("labels")

            cell(tokenField).bindText(setter = {
                service<AnthropicSetting>().saveApiToken(it)
                service<AnthropicSetting>().state.isTokenSet = true
                tokenField.text = ""
                tokenField.emptyText.text = AiCommentatorBundle.message("settings.token.placeholder.stored")
            }, getter = {
                ""
            }).columns(30).emptyText(
                if (service<AnthropicSetting>().state.isTokenSet) AiCommentatorBundle.message("settings.token.placeholder.stored") else AiCommentatorBundle.message(
                    "settings.token.placeholder"
                )
            ).applyToComponent {
                isEditable = true
            }.comment(AiCommentatorBundle.message("settings.anthropic.token.comment"), 30)
            contextHelp(AiCommentatorBundle.message("settings.token.help")).align(AlignX.LEFT)
        }
    }
}
