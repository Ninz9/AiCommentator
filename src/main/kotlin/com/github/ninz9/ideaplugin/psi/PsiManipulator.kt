package com.github.ninz9.ideaplugin.psi

import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil

interface PsiManipulator {

    fun getCaretMethod(offset: Int, file: PsiElement): PsiElement? {
        return findParentMethod(file.findElementAt(offset))
    }

    fun getCaretClass(offset: Int, file: PsiElement): PsiElement? {
        return findParentClass(file.findElementAt(offset))
    }

    fun findParentMethod(element: PsiElement?): PsiElement?

    fun findParentClass(element: PsiElement?): PsiElement?

    fun insertCommentBeforeElement(project: Project, element: PsiElement, comment: String)

    fun analyzePsiMethod(element: PsiElement): CodeStructure?

    fun analyzePsiClass(element: PsiElement): CodeStructure?

    fun deleteElementComment(project: Project, element: PsiElement) {
        WriteCommandAction.runWriteCommandAction(project) {
            var sibling = element.prevSibling
            while (sibling is PsiWhiteSpace) {
                sibling = sibling.prevSibling
            }
            if (sibling is PsiComment) {
                sibling.delete()
            } else {
                val innerComment = PsiTreeUtil.findChildOfType(element, PsiComment::class.java)
                innerComment?.delete()
            }
        }
    }
}
