package com.github.ninz9.ideaplugin.formatters

import com.intellij.testFramework.LightPlatformTestCase

class KotlinDocFormatterTest: LightPlatformTestCase() {
    private val formatter = KotlinDocFormatter()


    fun `test isValidKDoc returns true for valid KDoc`() {
        val validKDoc = """
            /**
             * This is a valid KDoc comment.
             *
             * @param name The name of the user.
             * @return The result of the operation.
             */
        """.trimIndent()


        assertTrue(formatter.isValidDoc(validKDoc, listOf("name"), true, emptyList(), emptyList()))
    }


    fun `test isValidKDoc returns false for invalid KDoc`() {
        val invalidKDoc = """
            /**
             * This is an invalid comment.
             * Missing closing tag.
        """.trimIndent()

        assertFalse(formatter.isValidDoc(invalidKDoc, listOf("name"), true, emptyList(), emptyList()))
    }

    fun `test hasAllParamsDocumented returns true when all parameters are documented`() {
        val kdoc = """
            /**
             * This is a method.
             * 
             * @param name The name of the user.
             */
        """.trimIndent()
        val paramNames = listOf("name")

        assertTrue(formatter.isValidDoc(kdoc, paramNames, false, emptyList(), emptyList()))
    }


    fun `test hasAllParamsDocumented returns false when a parameter is missing`() {
        val kdoc = """
            /**
             * This is a method.
             * 
             * @param name The name of the user.
             */
        """.trimIndent()
        val paramNames = listOf("name", "age")

        assertFalse(formatter.isValidDoc(kdoc, paramNames, false, emptyList(), emptyList()))
    }

    fun `test hasReturnDocumented returns true when @return is present`() {
        val kdoc = """
            /**
             * This is a method.
             * 
             * @return The result.
             */
        """.trimIndent()

        assertTrue(formatter.isValidDoc(kdoc, emptyList(), true, emptyList(), emptyList()))
    }


    fun `test hasReturnDocumented returns false when @return is missing for a non-Unit method`() {
        val kdoc = """
            /**
             * This is a method.
             */
        """.trimIndent()

        assertFalse(formatter.isValidDoc(kdoc, emptyList(), true, emptyList(), emptyList()))
    }


    fun `test hasAllPropertiesDocumented returns true when all properties are documented`() {
        val kdoc = """
            /**
             * This is a class.
             * 
             * @property name The name of the user.
             */
        """.trimIndent()
        val propertyNames = listOf("name")

        assertTrue(formatter.isValidDoc(kdoc, emptyList(), false, emptyList(), propertyNames))
    }


    fun `test hasAllPropertiesDocumented returns false when a property is missing`() {
        val kdoc = """
            /**
             * This is a class.
             * 
             * @property name The name of the user.
             */
        """.trimIndent()
        val propertyNames = listOf("name", "age")

        assertFalse(formatter.isValidDoc(kdoc, emptyList(), false, emptyList(), propertyNames))
    }


    fun `test formatDoc, input contain * and whitespaces`() {
        val kdoc = """
          
     
     
     
             * This is a method.
             * 
             * @param name The name of the user.
             
        """.trimIndent()

        val expected = "/**\n" +
                "* This is a method.\n" +
                "*\n" +
                "* @param name The name of the user.\n" +
                "*/"

        assertEquals(expected, formatter.formatDoc(kdoc))
    }


    fun `test formatDoc, input is text with special tags`() {
        val kdoc = """
          
     
     
     
              This is a method.
              
              @param name The name of the user.
              @return The result.
             
        """.trimIndent()

        val expected = "/**\n" +
                "* This is a method.\n" +
                "*\n" +
                "* @param name The name of the user.\n" +
                "* @return The result.\n" +
                "*/"

        assertEquals(expected, formatter.formatDoc(kdoc))
    }

    fun `test formatDoc, input is KDoc with tags and whitespaces`() {

        val kdoc = """
            /**
             * This is a method.
             * 
             * @param name The name of the user.
             * @return The result.
             */
        """.trimIndent()

        val expected = "/**\n" +
                "* This is a method.\n" +
                "*\n" +
                "* @param name The name of the user.\n" +
                "* @return The result.\n" +
                "*/"

        assertEquals(expected, formatter.formatDoc(kdoc))
    }

}
