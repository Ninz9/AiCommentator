package com.github.ninz9.aicommentator.configuration

import com.github.ninz9.aicommentator.AiCommentatorBundle
import com.github.ninz9.aicommentator.utils.positiveIntValidation
import com.github.ninz9.aicommentator.utils.temperatureValidation
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import kotlin.text.toDoubleOrNull

/**
 * Interface representing a settings panel for LLM (Large Language Models) configurations.
 * Contain the realization of common UI elements such as temperature field and max token field
 */
interface LLMSettingsPanel {

    fun Panel.temperatureRow(property: MutableProperty<Double>) {
        row {
            label(AiCommentatorBundle.message("settings.temperature")).widthGroup("labels")

            textField()
                .applyToComponent {
                    name = "temperatureField"
                }
                .columns(30)
                .bindText(setter = {
                    property.set(it.toDoubleOrNull() ?: 0.0)
                }, getter = {
                    property.get().toString()
                }).validationOnInput {
                    temperatureValidation(it.text)
                }.validationOnApply {
                    temperatureValidation(it.text)
                }
            contextHelp(AiCommentatorBundle.message("settings.temperature.help")).align(AlignX.LEFT)
        }
    }

    fun Panel.maxTokensField(property: MutableProperty<Int>) {
        row {
            label(AiCommentatorBundle.message("settings.max_tokens")).widthGroup("labels")

            textField()
                .applyToComponent {
                    name = "maxTokensField"
                }
                .columns(30)
                .bindIntText(property)
                .validationOnInput { positiveIntValidation(it.text) }
                .validationOnApply { positiveIntValidation(it.text) }
        }
    }
}