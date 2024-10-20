package com.github.ninz9.ideaplugin.psi

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType


class KotlinLangPsiManipulatorTest: BasePlatformTestCase() {
    private lateinit var manipulator: KotlinLangPsiManipulator

    override fun setUp() {
        super.setUp()
        manipulator = KotlinLangPsiManipulator()
    }

    fun testFindCaretMethod() {
        val file = myFixture.configureByText("Test.kt", """
            class TestClass {
                fun testMethod() {
                    val x = 1<caret>
                }
            }
        """.trimIndent())

        val parentMethod = manipulator.getCaretMethod(myFixture.caretOffset, file)

        assertNotNull(parentMethod)
        assertTrue(parentMethod is KtNamedFunction)
        assertEquals("testMethod", (parentMethod as KtNamedFunction).name)
    }

    fun testFindCaretMethodMustBeNull() {
        val file = myFixture.configureByText("Test.kt", """
            class TestClass {
                fun testMethod() {
                    val x = 1
                }
                <caret>
                fun testMethod2() {
                    val y = 2
                }
            }
        """.trimIndent())

        val parentMethod = manipulator.getCaretMethod(myFixture.caretOffset, file)

        assertNull(parentMethod)
    }

    fun testFindCaretClass() {
        val file = myFixture.configureByText("Test.kt", """
            class TestClass {
                fun testMethod() {
                    val x = 1<caret>
                }
            }
        """.trimIndent())

        val parentClass = manipulator.getCaretClass(myFixture.caretOffset, file)

        assertNotNull(parentClass)
        assertTrue(parentClass is KtClass)
        assertEquals("TestClass", (parentClass as KtClass).name)
    }

    fun testInsertCommentBeforeMethod() {
        val file = myFixture.configureByText("Test.kt", """
            class TestClass {
                fun testMethod() {
                    val x = 1
                }
            }
        """.trimIndent())

        val element = file.getChildOfType<KtClass>()?.body?.functions?.first()
        assertNotNull(element)

        runWriteAction {
            manipulator.insertCommentBeforeElement(project, element!!, "// This is a test comment",
                "I HOPE YOU A HAPPY TODAY, IN CASE YOU A DONT, YOU MUST TO KNOW, THAT I.... I WISH YOU HAPPINESS ")
        }

        myFixture.checkResult("""
            class TestClass {
                // This is a test comment
                fun testMethod() {
                    val x = 1
                }
            }
        """.trimIndent())
    }

    fun testInsertCommentBeforeClass() {
        val file = myFixture.configureByText("Test.kt", """
            class TestClass {
                fun testMethod() {
                    val x = 1
                }
            }
        """.trimIndent())

        val element = file.getChildOfType<KtClass>()
        assertNotNull(element)

        runWriteAction {
            manipulator.insertCommentBeforeElement(project, element!!, "// This is a test comment", "ANIGILATORNAJA_PUSHKA2000")
        }

        myFixture.checkResult("""
            // This is a test comment
            class TestClass {
                fun testMethod() {
                    val x = 1
                }
            }
        """.trimIndent())
    }

    fun testAnalyzePsiMethod() {
        val file = myFixture.configureByText("Test.kt", """
            class TestClass {
                fun testMethod(param1: Int, param2: String): Boolean {
                    if (param1 > 0) {
                        throw IllegalArgumentException("Invalid param1")
                    }
                    return true
                }
            }
        """.trimIndent())

        val method = file.getChildOfType<KtClass>()?.body?.functions?.first()
        assertNotNull(method)

        val codeStructure = manipulator.analyzePsiMethod(method!!)

        assertNotNull(codeStructure)
        assertEquals("Kotlin", codeStructure?.language)
        assertEquals(listOf("param1", "param2"), codeStructure?.paramNames)
        assertTrue(codeStructure?.hasReturnValue == true)
    }

    fun testAnalyzePsiClass() {
        val file = myFixture.configureByText("Test.kt", """
            class TestClass(val prop1: Int, var prop2: String) {
                val prop3 = "Hello"
            }
        """.trimIndent())

        val ktClass = file.getChildOfType<KtClass>()
        assertNotNull(ktClass)

        val codeStructure = manipulator.analyzePsiClass(ktClass!!)

        assertNotNull(codeStructure)
        assertEquals("Kotlin", codeStructure?.language)
        assertEquals(listOf("prop1", "prop2", "prop3"), codeStructure?.propertyNames)
    }

    private fun runWriteAction(action: () -> Unit) {
        WriteCommandAction.runWriteCommandAction(project) { action() }
    }
}