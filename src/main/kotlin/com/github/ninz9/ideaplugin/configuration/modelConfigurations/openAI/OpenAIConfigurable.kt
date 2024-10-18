package com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.github.ninz9.ideaplugin.configuration.LLMSettingsPanel
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.AvailableOpenAIModels
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
 * A configurable panel for OpenAI settings, providing UI components to set up the API token,
 * select the desired OpenAI model, and configure model-specific parameters like temperature and max tokens.
 *
 * This class extends BoundConfigurable and implements LLMSettingsPanel to create
 * a settings UI specific to OpenAI configurations.
 *
 * @constructor Creates an instance of OpenAIConfigurable.
 */
class OpenAIConfigurable : BoundConfigurable("OpenAI"), LLMSettingsPanel {

    private val tokenField: JBPasswordField = JBPasswordField()
    private var tokenIsSet = service<OpenAISetting>().state.isTokenSet

    private val currentModelComboBox: ComboBox<AvailableOpenAIModels> =
        ComboBox(EnumComboBoxModel(AvailableOpenAIModels::class.java))


    override fun createPanel(): DialogPanel {
        return panel {
            vendorRow()
            tokenRow()
            temperatureRow(service<OpenAISetting>().state::temperature.toMutableProperty())
            maxTokensField(service<OpenAISetting>().state::maxTokens.toMutableProperty())
        }
    }

    override fun apply() {
        super.apply()
    }

    fun Panel.vendorRow() {
        row {
            label(AiCommentatorBundle.message("settings.selected_model")).widthGroup("labels")
            cell(currentModelComboBox).columns(30).applyToComponent {
                isEditable = true
            }.bindItem(service<OpenAISetting>().state::model.toNullableProperty())
        }
    }

    fun Panel.tokenRow() {
        row {
            label(AiCommentatorBundle.message("settings.token")).widthGroup("labels")

            cell(tokenField).columns(30).bindText(setter = {
                service<OpenAISetting>().saveApiToken(it)
                tokenIsSet = true
                tokenField.text = ""
                tokenField.emptyText.text = AiCommentatorBundle.message("settings.token.placeholder.stored")
            }, getter = {
                ""
            }).emptyText(
                if (service<OpenAISetting>().state.isTokenSet)
                    AiCommentatorBundle.message("settings.token.placeholder.stored")
                else
                    AiCommentatorBundle.message("settings.token.placeholder")
            ).applyToComponent {
                isEditable = true
            }.comment(AiCommentatorBundle.message("settings.openAi.token.comment"), 30)
            contextHelp(AiCommentatorBundle.message("settings.token.help")).align(AlignX.LEFT)
        }
    }
}

