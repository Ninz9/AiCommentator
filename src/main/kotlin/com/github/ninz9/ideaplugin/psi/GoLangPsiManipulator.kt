//package com.github.ninz9.ideaplugin.psi
//
//import com.intellij.openapi.actionSystem.AnActionEvent
//import com.intellij.openapi.actionSystem.CommonDataKeys
//import com.intellij.psi.PsiElement
//
//class GoLangPsiManipulator: PsiManipulator {
//    override fun privateInsertCommentBeforeElement(
//        event: AnActionEvent,
//        element: PsiElement,
//        comment: String
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getCaretMethod(event: AnActionEvent): PsiElement? {
//        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
//        val file = event.getData(CommonDataKeys.PSI_FILE) ?: return null
//
//        val offset = editor.caretModel.offset
//
//        val element = file.findElementAt(offset) ?: return null
//
//        return null
//    }
//
//    override fun getSelectedText(event: AnActionEvent): String {
//        TODO("Not yet implemented")
//    }
//
//    override fun getCaretClass(event: AnActionEvent): PsiElement? {
//        TODO("Not yet implemented")
//    }
//
//    override fun insertCommentBeforeElement(
//        event: AnActionEvent,
//        element: PsiElement,
//        comment: String
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun findParentMethod(element: PsiElement?): PsiElement? {
//        TODO("Not yet implemented")
//    }
//
//    override fun findParentClass(element: PsiElement?): PsiElement? {
//        TODO("Not yet implemented")
//    }
//}
