package com.github.ninz9.ideaplugin.utils

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.layout.ValidationInfoBuilder
import com.intellij.util.ui.ComponentWithEmptyText
import javax.swing.JComponent

fun ValidationInfoBuilder.temperatureValidation(temperature: String): ValidationInfo? {
    if (temperature.isNotBlank()){
        temperature.toDoubleOrNull().let {
            if (it != null && it in 0.0..1.0) {
                return null
            }
        }
    }

    return error(AiCommentatorBundle.message("settings.validation.temperature"))
}

fun ValidationInfoBuilder.positiveIntValidation(text: String): ValidationInfo? {
   if (text.isNotBlank()) {
       text.toIntOrNull().let {
           if (it != null && it > 0) {
               return null
           }
       }
   }
    return error(AiCommentatorBundle.message("settings.validation.positive_integer"))
}


fun <T>Cell<T>.emptyText(emptyText: String) : Cell<T> where T : JComponent, T : ComponentWithEmptyText {
    this.component.emptyText.text = emptyText
    return this
}