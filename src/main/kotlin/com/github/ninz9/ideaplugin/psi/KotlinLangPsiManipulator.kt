package com.github.ninz9.ideaplugin.psi

import com.github.ninz9.ideaplugin.utils.types.MethodStructure
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtThrowExpression


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
                deleteElementComment(project, element)
                parent.addBefore(commentElement, element)
//                project.service<CodeStyleManager>().reformat(commentElement)
            }
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
    }


    override fun addTextChunkToComment(
        project: Project,
        element: PsiElement,
        text: String
    ) {
        val factory = KtPsiFactory(project)
        WriteCommandAction.runWriteCommandAction(project) {
            val newComment = factory.createComment(text)
            element.replace(newComment)
            project.service<CodeStyleManager>().reformat(element)
        }
    }


    override fun analyzePsiMethod(element: PsiElement): MethodStructure? {
        if (element !is KtNamedFunction) {
            return null
        }

        return ApplicationManager.getApplication().runReadAction<MethodStructure> {
            MethodStructure(
                code = element.text,
                language = element.language.displayName,
                complexity = "Complexity",
                paramNames = element.valueParameters.mapNotNull { it.text },
                hasReturnValue = element.hasDeclaredReturnType(),
                exceptionNames = element.bodyBlockExpression
                    ?.children
                    ?.filterIsInstance<KtThrowExpression>()
                    ?.mapNotNull { it.text }
                    .orEmpty()
            )
        }
    }

    override fun analyzePsiClass(element: PsiElement): MethodStructure? {
        if (element !is KtClass) {
            return null
        }

        return ApplicationManager.getApplication().runReadAction<MethodStructure> {
            MethodStructure(
                code = element.text,
                language = element.language.displayName,
                complexity = "Complexity",
                paramNames = emptyList(),
                hasReturnValue = false,
                exceptionNames = emptyList()
            )
        }
    }

    override fun replaceCommentText(
        project: Project,
        element: PsiElement,
        text: String
    ) {
        if (element !is PsiComment) return

        val factory = KtPsiFactory(project)
        WriteCommandAction.runWriteCommandAction(project) {
            val newComment = factory.createComment(text)
            element.replace(newComment)
//            project.service<CodeStyleManager>().reformat(element)
        }
    }


    override fun deleteElementComment(
        project: Project,
        element: PsiElement
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            try {
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
            } catch (e: Exception) {
                println("Error while deleting comment: ${e.message}")
            }
        }
    }
}

