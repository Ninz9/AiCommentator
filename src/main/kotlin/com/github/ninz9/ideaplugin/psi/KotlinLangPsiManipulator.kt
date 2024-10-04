package com.github.ninz9.ideaplugin.psi

import com.github.ninz9.ideaplugin.utils.readText
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory


@Service
class KotlinLangPsiManipulator : PsiManipulator {

    override fun findParentMethod(element: PsiElement?): PsiElement? {
        if (element == null) return null
        return PsiTreeUtil.getParentOfType(element, KtNamedFunction::class.java)
    }

    override fun findParentClass(element: PsiElement?): PsiElement? {
        if (element == null) return null
        return PsiTreeUtil.getParentOfType(element, KtClass::class.java)
    }

    override fun insertCommentBeforeElement(
        project: Project,
        element: PsiElement,
        comment: String
    ) {
        val factory = KtPsiFactory(project)

        WriteCommandAction.runWriteCommandAction(project) {
            val commentElement = factory.createComment(comment)
            val parent = element.parent
            if (parent.containingFile != null && parent != null) {
                parent.addBefore(commentElement, element)
            }
        }
    }

    override fun createCommentElement(project: Project): PsiElement? {
        val factory = KtPsiFactory(project)
        var element: PsiElement? = null
        WriteCommandAction.runWriteCommandAction(project) {
            element = factory.createComment("")
        }
        return element
    }

    override fun addTextChunkToComment(
        project: Project,
        comment: PsiElement,
        text: String
    ) {
        val factory = KtPsiFactory(project)
        val prevText = comment.readText().replace("//", "").replace("/*", "").replace("*/", "")

        val newText = "$prevText $text"
        WriteCommandAction.runWriteCommandAction(project) {
            val textElement = factory.createComment(newText)
            comment.replace(textElement)
        }
    }
}
