package com.github.ninz9.aicommentator.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CreateCommentForMethodStreamTest: BasePlatformTestCase() {
    private lateinit var action: CreateCommentForMethodStream

    override fun setUp() {
        super.setUp()
        action = CreateCommentForMethodStream()

    }

    fun doTestVisibility(file: String) {
        val psiFile = myFixture.configureByText("TestClass.java", file.trimIndent())

        val dataContext = SimpleDataContext.builder()
            .add(CommonDataKeys.PROJECT, project)
            .add(CommonDataKeys.EDITOR, myFixture.editor)
            .add(CommonDataKeys.PSI_FILE, psiFile)
            .build()

        val event = AnActionEvent.createFromDataContext("test", null, dataContext)

        action.update(event)

        assertTrue("Action should be enabled and visible when cursor is inside a method", event.presentation.isEnabledAndVisible)
    }

    fun doTestHiddenness(file: String) {
        val psiFile = myFixture.configureByText("TestClass.java", file.trimIndent())

        val dataContext = SimpleDataContext.builder()
            .add(CommonDataKeys.PROJECT, project)
            .add(CommonDataKeys.EDITOR, myFixture.editor)
            .add(CommonDataKeys.PSI_FILE, psiFile)
            .build()

        val event = AnActionEvent.createFromDataContext("test", null, dataContext)

        action.update(event)

        assertFalse("Action should be disabled and invisible when cursor is not inside a method", event.presentation.isEnabledAndVisible)
    }


    fun testVisibilityJava1() {
        doTestVisibility("""
            public class TestClass {
                public void testMethod1() {
                    int x = 1<caret>;
                }
                
                public void testMethod2() {
                    int y = 2;
                }
            }
        """)
    }

    fun testVisibilityJava2() {
        doTestVisibility("""
            public class TestClass {
                public void testMethod1() {
                    int x = 1;
                }
                
                public <caret>void testMethod2() {
                    int y = 2;
                }
            }
        """)

    }

    fun testVisibilityJava3() {
        doTestVisibility("""
            public class TestClass {
                public void testMethod1() {
                    int x = 1;
                }
                
                public void testMethod2() {
                    int y = 2;
                <caret>}
            }
            
        """)

    }

    fun testVisibilityKotlin1() {
        doTestVisibility("""
            class TestClass {
                fun testMethod1() {
                    val x = 1<caret>;
                }
                
                fun testMethod2() {
                    val y = 2;
                }
            }
        """)

    }

    fun testVisibilityKotlin2() {
        doTestVisibility("""
            class TestClass {
                fun testMethod1() {
                    val x = 1;
                }
                
                fun <caret>testMethod2() {
                    val y = 2;
                }
            }
        """)

    }

    fun testVisibilityKotlin3() {
        doTestVisibility("""
            class TestClass {
                fun testMethod1() {
                    val x = 1;
                }
                
                fun testMethod2() {
                    val y = 2;
                <caret>}
            }
        """)

    }

     fun testHiddennessJava1() {
        doTestHiddenness("""
            public class TestClass {
                <caret>
                public void testMethod1() {
                    int x = 1;
                }
                
                public void testMethod2() {
                    int y = 2;
                }
            }
        """)

     }

    fun testHiddennessJava2() {
        doTestHiddenness("""
            <caret>public class TestClass {
                public void testMethod1() {
                    int x = 1;
                }
                
                public void testMethod2() {
                    int y = 2;
                }
            }
        """)

    }

    fun testHiddennessJava3() {

        doTestHiddenness("""
            public class TestClass {
                public void testMethod1() {
                    int x = 1;
                }
                
                public void testMethod2() {
                    int y = 2;
                }
            <caret>}
        """)

    }

    fun testHiddennessKotlin1() {
        doTestHiddenness("""
            class TestClass {
                <caret>
                fun testMethod1() {
                    val x = 1;
                }
                
                fun testMethod2() {
                    val y = 2;
                }
            }
        """)


    }

    fun testHiddennessKotlin2() {
        doTestHiddenness("""
            <caret>class TestClass {
                fun testMethod1() {
                    val x = 1;
                }
                
                fun testMethod2() {
                    val y = 2;
                }
            }
        """)

    }

    fun testHiddennessKotlin3() {

        doTestHiddenness("""
            class TestClass {
                fun testMethod1() {
                    val x = 1;
                }
                
                fun testMethod2() {
                    val y = 2;
                }
            <caret>}
        """)

    }
}