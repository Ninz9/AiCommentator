package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.MyBundle
import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty


class PluginConfigurable: BoundConfigurable(MyBundle.message("name")) {

    private var currentVendorComboBox: ComboBox<AiModel> =
        ComboBox(AiModel.entries.toTypedArray())

    override fun createPanel(): DialogPanel {
        return panel {
            vendorRow()
            separator()
        }
    }

    fun Panel.vendorRow() {
        row {
            label(MyBundle.message("settings.selected_model")).widthGroup("labels")

            cell(currentVendorComboBox)
                .bindItem(service<PluginSettings>().state::currentModel.toNullableProperty())
        }
    }
}
