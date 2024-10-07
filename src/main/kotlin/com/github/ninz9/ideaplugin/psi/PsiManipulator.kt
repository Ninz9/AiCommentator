package com.github.ninz9.ideaplugin.psi


import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.editor.fixers.end
import org.jetbrains.kotlin.idea.editor.fixers.start
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset


interface PsiManipulator {

    fun getCaretMethod(offset: Int, file: PsiElement): PsiElement? {
        return findParentMethod(file.findElementAt(offset))
    }

    fun getCaretClass(offset: Int, file: PsiElement): PsiElement? {
        return findParentClass(file.findElementAt(offset))
    }

    fun getSelectedText(editor: Editor): String {
        return editor.selectionModel.selectedText ?: ""
    }

    fun findParentMethod(element: PsiElement?): PsiElement?

    fun findParentClass(element: PsiElement?): PsiElement?

    fun insertCommentBeforeElement(project: Project, element: PsiElement, comment: String)

    fun createCommentElement(project: Project): PsiElement?

    fun addTextChunkToComment(project: Project, comment: PsiElement, text: String)
}
