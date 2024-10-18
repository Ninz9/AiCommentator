package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty


/**
 * A configurable settings panel for a plugin, implemented as a `BoundConfigurable`.
 *
 * This class defines the UI for configuring plugin settings, including a combo box
 * for selecting the AI model vendor. It extends BoundConfigurable to automatically
 * bind UI elements to the underlying settings state.
 *
 * The settings panel contains:
 * - A row with a label and a combo box for selecting the AI model vendor.
 * - A separator for visual separation of different setting sections.
 *
 * The bound UI elements are automatically synchronized with the plugin settings state.
 */

class PluginConfigurable: BoundConfigurable(AiCommentatorBundle.message("name")) {

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
            label(AiCommentatorBundle.message("settings.selected_model")).widthGroup("labels")

            cell(currentVendorComboBox)
                .bindItem(service<PluginSettings>().state::currentModel.toNullableProperty())
        }
    }
}
