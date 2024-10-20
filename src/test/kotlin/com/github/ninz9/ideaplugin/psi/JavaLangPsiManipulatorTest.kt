package com.github.ninz9.ideaplugin.psi

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaLangPsiManipulatorTest: BasePlatformTestCase() {
    private lateinit var manipulator: JavaLangPsiManipulator

    override fun setUp() {
        super.setUp()
        manipulator = JavaLangPsiManipulator()
    }

    fun testFindCaretMethod() {
        val file = myFixture.configureByText("TestClass.java", """
            public class TestClass {
                public void testMethod1() {
                    int x = 1<caret>;
                }
                
                public void testMethod2() {
                    int y = 2;
                }
            }
        """.trimIndent())

        val parentMethod = manipulator.getCaretMethod(myFixture.caretOffset, file)

        assertNotNull(parentMethod)
        assertTrue(parentMethod is PsiMethod)
        assertEquals("testMethod1", (parentMethod as PsiMethod).name)
    }

    fun testFindCaretMethodMustBeNull() {
        val file = myFixture.configureByText("TestClass.java", """
            public class TestClass {
                public void testMethod1() {
                    int x = 1;
                }
                <caret>
                public void testMethod2() {
                    int y = 2;
                }
            }
        """.trimIndent())

        val parentMethod = manipulator.getCaretMethod(myFixture.caretOffset, file)

        assertNull(parentMethod)
    }

    fun testFindCaretClass() {
        val file = myFixture.configureByText("TestClass.java", """
            public class TestClass {
                public void testMethod() {
                    int x = 1<caret>;
                }
            }
        """.trimIndent())

        val parentClass = manipulator.getCaretClass(myFixture.caretOffset, file)

        assertNotNull(parentClass)
        assertTrue(parentClass is PsiClass)
        assertEquals("TestClass", (parentClass as PsiClass).name)
    }

    fun testFindCaretClassMustBeNull() {
        val file = myFixture.configureByText("TestClass.java", """
            public class TestClass {
                public void testMethod() {
                    int x = 1;
                }
            }
            <caret>
        """.trimIndent())

        val parentClass = manipulator.getCaretClass(myFixture.caretOffset, file)

        assertNull(parentClass)
    }



    fun testInsertCommentBeforeMethod() {
        val file = myFixture.configureByText("TestClass.java", """
            public class TestClass {
                public void testMethod() {
                    int x = 1;
                }
            }
        """.trimIndent())

        val element = PsiTreeUtil.getChildOfType(file, PsiClass::class.java)?.methods?.get(0)
        assertNotNull(element)

        WriteCommandAction.runWriteCommandAction(project) {
            manipulator.insertCommentBeforeElement(project, element!!, "/** This is a test comment */", "KEKW")
        }

        println(myFixture.toString())
        myFixture.checkResult("""
            public class TestClass {
                /** This is a test comment */
                public void testMethod() {
                    int x = 1;
                }
            }
        """.trimIndent())
    }

    fun testInsertCommentBeforeClass() {
        val file = myFixture.configureByText(
            "TestClass.java", """
            public class TestClass {
                public void testMethod() {
                    int x = 1;
                }
            }
        """.trimIndent()
        )

        val element = PsiTreeUtil.getChildOfType(file, PsiClass::class.java)
        assertNotNull(element)

        WriteCommandAction.runWriteCommandAction(project) {
            manipulator.insertCommentBeforeElement(project, element!!, "/** This is a test comment */", "I HAVE A PEN, I HAVE AN APPLE, HWWWW APPLE PAN")
        }

        myFixture.checkResult("""
            /** This is a test comment */
            
            public class TestClass {
                public void testMethod() {
                    int x = 1;
                }
            }
        """.trimIndent())
    }

    fun testAnalyzePsiMethod() {
        val file = myFixture.configureByText("TestClass.java", """
            public class TestClass {
                public boolean testMethod(int param1, String param2) throws IllegalArgumentException {
                    if (param1 > 0) {
                        throw new IllegalArgumentException("Invalid param1");
                    }
                    return true;
                }
            }
        """.trimIndent())

        val method = (PsiTreeUtil.getChildOfType(file, PsiClass::class.java))?.methods?.get(0)
        assertNotNull(method)

        val codeStructure = manipulator.analyzePsiMethod(method!!)

        assertNotNull(codeStructure)
        assertEquals("Java", codeStructure?.language)
        assertEquals(listOf("param1", "param2"), codeStructure?.paramNames)
        assertTrue(codeStructure?.hasReturnValue == true)
        assertTrue(codeStructure?.exceptionNames?.contains("IllegalArgumentException") == true)
    }

    fun testAnalyzePsiClass() {
        val file = myFixture.configureByText("TestClass.java", """
            public class TestClass {
                private int field1;
                public String field2;
            }
        """.trimIndent())

        val psiClass = PsiTreeUtil.getChildOfType(file, PsiClass::class.java)
        assertNotNull(psiClass)

        val codeStructure = manipulator.analyzePsiClass(psiClass!!)

        assertNotNull(codeStructure)
        assertEquals("Java", codeStructure?.language)
        assertEquals(listOf("field1", "field2"), codeStructure?.propertyNames)
    }
}