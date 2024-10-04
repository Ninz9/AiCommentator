package com.github.ninz9.ideaplugin.ui.configUiComponents

import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.ui.dsl.builder.Row

internal val models = AiModel.entries.map { it.displayedName }.toTypedArray()

internal val modelsOptionDescriptors = AiModel.values().map { it.displayedName }

internal fun Row.ModelsComboBox() {



}
