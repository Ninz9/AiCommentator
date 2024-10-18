package com.github.ninz9.ideaplugin.configuration.modelConfigurations.anthropic

import com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic.AvailableAnthropicModels
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.replaceService
import com.intellij.ui.components.JBPasswordField
import javax.swing.JComboBox

class AnthropicConfigurableTest: BasePlatformTestCase() {
    lateinit var anthropicConfigurable: AnthropicConfigurable
    

    override fun setUp() {
        super.setUp()
        
        val anthropicSetting = AnthropicSetting()

        ApplicationManager.getApplication().replaceService(
            AnthropicSetting::class.java,
            anthropicSetting,
            testRootDisposable
        )
        
        anthropicConfigurable = AnthropicConfigurable()
    }
    
    
    fun testCreatePanel() {
        val panel = anthropicConfigurable.createPanel()
        assertNotNull(panel)
        assertTrue(panel is DialogPanel)
    }
    
    fun testVendorRowInitialization() {
        val panel = anthropicConfigurable.createPanel()
        val comboBox = findComponentByClass(panel, JComboBox::class.java)
        assertNotNull(comboBox)
        assertEquals(AvailableAnthropicModels.entries.size, comboBox?.itemCount)
    }
    
    fun testTokenRowInitialization() {
        ApplicationManager.getApplication().getService(AnthropicSetting::class.java).state.isTokenSet = false
        val panel = anthropicConfigurable.createPanel()
        val tokenField = findComponentByClass(panel, JBPasswordField::class.java)
        assertNotNull(tokenField)
        assertEquals("", tokenField)
    }
    
    fun testTokenRowWithSetToken() {
        ApplicationManager.getApplication().getService(AnthropicSetting::class.java).state.isTokenSet = true
        val panel = anthropicConfigurable.createPanel()
        val tokenField = findComponentByName(panel, "tokenField")
        assertNotNull(tokenField)
        assertEquals("", tokenField)
    }
    
   fun testSaveToken() {
        ApplicationManager.getApplication().getService(AnthropicSetting::class.java).state.isTokenSet = false
        val panel = anthropicConfigurable.createPanel()
        val tokenField = findComponentByClass(panel, JBPasswordField::class.java)
        assertNotNull(tokenField)

        tokenField?.text = "test-token"

        anthropicConfigurable.apply()

        assertTrue(ApplicationManager.getApplication().getService(AnthropicSetting::class.java).state.isTokenSet)
    }
    
    fun testTemperatureChange() {
        val panel = anthropicConfigurable.createPanel()
        val temperatureComponent = findComponentByName(panel, "temperatureField")
        assertNotNull(temperatureComponent)


        (temperatureComponent as javax.swing.JTextField).text = 0.5.toString()

        anthropicConfigurable.apply()

        assertEquals(
            0.5,
            ApplicationManager.getApplication().getService(AnthropicSetting::class.java).state.temperature,
            0.01
        )
    }

    fun testMaxTokensChange() {
        val panel = anthropicConfigurable.createPanel()
        val maxTokensField = findComponentByName(panel, "maxTokensField")
        assertNotNull(maxTokensField)

        (maxTokensField as javax.swing.JTextField).text = "1000"

        anthropicConfigurable.apply()

        assertEquals(1000, ApplicationManager.getApplication().getService(AnthropicSetting::class.java).state.maxTokens)
    }
    
    
    
    private fun findComponentByName(panel: DialogPanel, name: String): Any? {
        return panel.components.find { it.name == name }
    }

    private fun <T> findComponentByClass(panel: DialogPanel, clazz: Class<T>): T? {
        return panel.components.find { clazz.isInstance(it) } as T?
    }
    
}