package com.github.ninz9.ideaplugin.psi

import com.github.ninz9.ideaplugin.utils.types.MethodStructure
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
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
            if (parent.containingFile != null && parent != null)
                parent.addBefore(commentElement, element)
        }
    }

    override fun createCommentElement(project: Project, element: PsiElement): PsiElement? {
        val factory = KtPsiFactory(project)
        var comment: PsiComment? = null
        WriteCommandAction.runWriteCommandAction(project) {
            comment = factory.createComment("/** \n * \n */")
            val parent = element.parent
            if (parent.containingFile != null && parent != null)
                parent.addBefore(comment!!, element)
        }
        return comment

//        val factory = KtPsiFactory(project)
//        WriteCommandAction.runWriteCommandAction(project) {
//
//            val newComment = factory.createComment("/** \n * \n */")
//            val parent = element.parent
//            parent.addBefore(newComment, element)
//
//        }


    }


    override fun addTextChunkToComment(
        project: Project,
        element: PsiElement,
        text: String
    ) {
//        val factory = KtPsiFactory(project)
//
//        WriteCommandAction.runWriteCommandAction(project) {
//            val prevText = comment.readText().replace("/**", "").replace("*/", "").replace("*", "")
//            val newText = "//* \n* $prevText $text */"
//            val textElement = factory.createComment(newText)
//            comment.replace(textElement)
//        }

        val factory = KtPsiFactory(project)
        WriteCommandAction.runWriteCommandAction(project) {

            val newComment = factory.createComment(text)
            element.replace(newComment)

            val parent = element.parent
            CodeStyleManager.getInstance(project).reformat(parent)
        }

    }
}

