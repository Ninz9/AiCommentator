package com.github.ninz9.ideaplugin.configuration.modelConfigurations.openAI

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.github.ninz9.ideaplugin.llm.modelsImpl.openAI.AvailableOpenAIModels
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.replaceService
import com.intellij.ui.components.JBPasswordField
import javax.swing.JComboBox

class OpenAIConfigurableTest : BasePlatformTestCase() {

    private lateinit var openAIConfigurable: OpenAIConfigurable

    override fun setUp() {
        super.setUp()

        val openAISetting = OpenAISetting()
        ApplicationManager.getApplication().replaceService(
            OpenAISetting::class.java,
            openAISetting,
            testRootDisposable
        )

        openAIConfigurable = OpenAIConfigurable()
    }

    fun testCreatePanel() {
        val panel = openAIConfigurable.createPanel()
        assertNotNull(panel)
        assertTrue(panel is DialogPanel)
    }

    fun testVendorRowInitialization() {
        val panel = openAIConfigurable.createPanel()
        val comboBox = findComponentByClass(panel, JComboBox::class.java)
        assertNotNull(comboBox)
        assertEquals(AvailableOpenAIModels.entries.size, comboBox?.itemCount)
    }

    fun testTokenRowInitialization() {
        ApplicationManager.getApplication().getService(OpenAISetting::class.java).state.isTokenSet = false
        val panel = openAIConfigurable.createPanel()
        val tokenField = findComponentByClass(panel, JBPasswordField::class.java)
        assertNotNull(tokenField)
        assertEquals("", String(tokenField?.password ?: charArrayOf()))
        assertEquals(AiCommentatorBundle.message("settings.token.placeholder"), tokenField?.emptyText?.text)
    }

    fun testTokenRowWithSetToken() {
        ApplicationManager.getApplication().getService(OpenAISetting::class.java).state.isTokenSet = true
        val panel = openAIConfigurable.createPanel()
        val tokenField = findComponentByClass(panel, JBPasswordField::class.java)
        assertNotNull(tokenField)
        assertEquals("", String(tokenField?.password ?: charArrayOf()))
        assertEquals(AiCommentatorBundle.message("settings.token.placeholder.stored"), tokenField?.emptyText?.text)
    }

    fun testSaveToken() {
        ApplicationManager.getApplication().getService(OpenAISetting::class.java).state.isTokenSet = false
        val panel = openAIConfigurable.createPanel()
        val tokenField = findComponentByClass(panel, JBPasswordField::class.java)
        assertNotNull(tokenField)

        tokenField?.text = "test-token"

        openAIConfigurable.apply()

        assertTrue(ApplicationManager.getApplication().getService(OpenAISetting::class.java).state.isTokenSet)
    }

    fun testTemperatureChange() {
        val panel = openAIConfigurable.createPanel()
        val temperatureComponent = findComponentByName(panel, "temperatureField")
        assertNotNull(temperatureComponent)


        (temperatureComponent as javax.swing.JTextField).text = 0.5.toString()

        openAIConfigurable.apply()

        assertEquals(
            0.5,
            ApplicationManager.getApplication().getService(OpenAISetting::class.java).state.temperature,
            0.01
        )
    }

    fun testMaxTokensChange() {
        val panel = openAIConfigurable.createPanel()
        val maxTokensField = findComponentByName(panel, "maxTokensField")
        assertNotNull(maxTokensField)

        (maxTokensField as javax.swing.JTextField).text = "1000"

        openAIConfigurable.apply()

        assertEquals(1000, ApplicationManager.getApplication().getService(OpenAISetting::class.java).state.maxTokens)
    }

    private fun findComponentByName(panel: DialogPanel, name: String): Any? {
        return panel.components.find { it.name == name }
    }

    private fun <T> findComponentByClass(panel: DialogPanel, clazz: Class<T>): T? {
        return panel.components.find { clazz.isInstance(it) } as T?
    }
}