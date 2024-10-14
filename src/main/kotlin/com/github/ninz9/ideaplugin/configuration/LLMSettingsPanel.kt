package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.MyBundle
import com.github.ninz9.ideaplugin.utils.positiveIntValidation
import com.github.ninz9.ideaplugin.utils.temperatureValidation
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import kotlin.text.toDoubleOrNull

interface LLMSettingsPanel {

    fun Panel.temperatureRow(property: MutableProperty<Double>) {
        row {
            label(MyBundle.message("settings.temperature")).widthGroup("labels")
            textField().columns(30).bindText(setter = {
                property.set(it.toDoubleOrNull() ?: 0.0)
            }, getter = {
                property.get().toString()
            }).validationOnInput {
                temperatureValidation(it.text)
            }.validationOnApply {
                    temperatureValidation(it.text)
                }
            contextHelp(MyBundle.message("settings.temperature.help")).align(AlignX.LEFT)
        }
    }

    fun Panel.maxTokensField(property: MutableProperty<Int>) {
        row {
            label("Max tokens").widthGroup("labels")

            textField()
                .columns(30)
                .bindIntText(property)
                .validationOnInput { positiveIntValidation(it.text) }
                .validationOnApply { positiveIntValidation(it.text) }
        }
    }
}