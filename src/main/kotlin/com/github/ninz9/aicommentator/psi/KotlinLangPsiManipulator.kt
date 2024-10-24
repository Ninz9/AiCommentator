package com.github.ninz9.aicommentator.psi

import com.github.ninz9.aicommentator.utils.types.CodeStructure
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.startOffset


/**
 * KotlinLangPsiManipulator is a service class that provides various manipulations
 * and analyses on Kotlin PSI elements.
 */
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
        comment: String,
        transactionId: String,
    ) {
        val factory = KtPsiFactory(project)
        val editor = FileEditorManager.getInstance(project).selectedTextEditor

        WriteCommandAction.writeCommandAction(project).withGroupId(transactionId).run<Throwable> {
            val commentElement = factory.createComment(comment)
            val parent = element.parent
            if (parent.containingFile != null && parent != null) {
                deleteElementComment(project, element)
                parent.addBefore(commentElement, element)
                val logicalPosition =
                    editor?.offsetToLogicalPosition(element.startOffset) ?: return@run
                editor.scrollingModel.scrollTo(logicalPosition, ScrollType.MAKE_VISIBLE)
            }
        }
    }

    override fun analyzePsiMethod(element: PsiElement): CodeStructure? {
        if (element !is KtNamedFunction) {
            return null
        }

        return ApplicationManager.getApplication().runReadAction<CodeStructure> {
            CodeStructure(
                code = element.text,
                language = element.language.displayName,
                paramNames = element.valueParameters.mapNotNull { it.name },
                hasReturnValue = element.hasDeclaredReturnType(),
            )
        }
    }

    override fun analyzePsiClass(element: PsiElement): CodeStructure? {
        if (element !is KtClass) {
            return null
        }

        return ApplicationManager.getApplication().runReadAction<CodeStructure> {
            val primaryConstructorProperties = element.primaryConstructorParameters.mapNotNull { it.name }
            val bodyProperties = element.getProperties().mapNotNull { it.name }
            val properties = primaryConstructorProperties + bodyProperties

            CodeStructure(
                code = element.text,
                language = element.language.displayName,
                propertyNames = properties
            )
        }
    }
}
