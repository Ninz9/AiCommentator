package com.github.ninz9.aicommentator.utils

import com.github.ninz9.aicommentator.AiCommentatorBundle
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


/**
 * Sets the empty text of the component contained within the cell.
 *
 * @param emptyText the string to be displayed as empty text
 * @return the current cell for further configuration
 */
fun <T>Cell<T>.emptyText(emptyText: String) : Cell<T> where T : JComponent, T : ComponentWithEmptyText {
    this.component.emptyText.text = emptyText
    return this
}