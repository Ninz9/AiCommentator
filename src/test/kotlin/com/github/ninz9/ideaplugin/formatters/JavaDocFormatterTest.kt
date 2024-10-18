package com.github.ninz9.ideaplugin.formatters

import com.intellij.testFramework.LightPlatformTestCase


class JavaDocFormatterTest : LightPlatformTestCase() {

    private val formatter = JavaDocFormatter()

    fun `test isValidJavaDoc returns true for valid JavaDoc`() {
        val validJavaDoc = """
            /**
             * This is a valid JavaDoc comment.
             *
             * @param name The name of the user.
             * @return The result of the operation.
             * @throws IllegalArgumentException If an argument is invalid.
             */
        """.trimIndent()

        assertTrue(formatter.isValidDoc(validJavaDoc, listOf("name"), true, listOf("IllegalArgumentException"), emptyList()))
    }

    fun `test isValidJavaDoc returns false for invalid JavaDoc`() {
        val invalidJavaDoc = """
            /**
             * This is an invalid comment.
             * Missing closing tag.
        """.trimIndent()

        assertFalse(formatter.isValidDoc(invalidJavaDoc, listOf("name"), true, emptyList(), emptyList()))
    }

    fun `test hasAllParamsDocumented returns true when all parameters are documented`() {
        val javaDoc = """
            /**
             * This is a method.
             * 
             * @param name The name of the user.
             */
        """.trimIndent()
        val paramNames = listOf("name")

        assertTrue(formatter.isValidDoc(javaDoc, paramNames, false, emptyList(), emptyList()))
    }

    fun `test hasAllParamsDocumented returns false when a parameter is missing`() {
        val javaDoc = """
            /**
             * This is a method.
             * 
             * @param name The name of the user.
             */
        """.trimIndent()
        val paramNames = listOf("name", "age")

        assertFalse(formatter.isValidDoc(javaDoc, paramNames, false, emptyList(), emptyList()))
    }

    fun `test hasReturnDocumented returns true when return is present`() {
        val javaDoc = """
            /**
             * This is a method.
             * 
             * @return The result.
             */
        """.trimIndent()

        assertTrue(formatter.isValidDoc(javaDoc, emptyList(), true, emptyList(), emptyList()))
    }

    fun `test hasReturnDocumented returns false when return is missing for a non-void method`() {
        val javaDoc = """
            /**
             * This is a method.
             */
        """.trimIndent()

        assertFalse(formatter.isValidDoc(javaDoc, emptyList(), true, emptyList(), emptyList()))
    }

    fun `test hasAllPropertiesDocumented returns true when all properties are documented`() {
        val javaDoc = """
            /**
             * This is a class.
             * 
             * @property name The name of the user.
             */
        """.trimIndent()
        val propertyNames = listOf("name")

        assertTrue(formatter.isValidDoc(javaDoc, emptyList(), false, emptyList(), propertyNames))
    }

    fun `test hasAllPropertiesDocumented returns false when a property is missing`() {
        val javaDoc = """
            /**
             * This is a class.
             * 
             * @property name The name of the user.
             */
        """.trimIndent()
        val propertyNames = listOf("name", "age")

        assertFalse(formatter.isValidDoc(javaDoc, emptyList(), false, emptyList(), propertyNames))
    }

    fun `test formatDoc, input contain * and whitespaces`() {
        val javaDoc = """
           
           
           
             * This is a method.
             * 
             * @param name The name of the user.
             */
        """.trimIndent()

        val expectedJavaDoc = "/**\n" +
             "* This is a method.\n" +
             "*\n" +
             "* @param name The name of the user.\n" +
             "*/"

        assertEquals(expectedJavaDoc, formatter.formatDoc(javaDoc))
    }

    fun `test formatDoc, input is text with special tags`() {
        val javaDoc = """
            
                           This is a method.@param name The name of the user.
              @return The result.
             
        """.trimIndent()

        val expectedJavaDoc = "/**\n" +
             "* This is a method.\n" +
             "*\n" +
             "* @param name The name of the user.\n" +
             "* @return The result.\n" +
             "*/"

        assertEquals(expectedJavaDoc, formatter.formatDoc(javaDoc))
    }

    fun `test formatDoc, input is JavaDoc with tags and whitespaces`() {
        val javaDoc = """
            /**
             * This is a method.
             * 
             * @param name The name of the user.
             * @return The result.
             */
        """.trimIndent()

        val expectedJavaDoc = "/**\n" +
             "* This is a method.\n" +
             "*\n" +
             "* @param name The name of the user.\n" +
             "* @return The result.\n" +
             "*/"

        assertEquals(expectedJavaDoc, formatter.formatDoc(javaDoc))
    }
}