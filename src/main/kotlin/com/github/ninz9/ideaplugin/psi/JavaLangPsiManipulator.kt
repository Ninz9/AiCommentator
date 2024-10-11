package com.github.ninz9.ideaplugin.psi

import com.github.ninz9.ideaplugin.utils.types.MethodStructure
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil


@Service
class JavaLangPsiManipulator : PsiManipulator {

    override fun findParentMethod(element: PsiElement?): PsiElement? {
        if (element == null) return null
        return PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)
    }

    override fun findParentClass(element: PsiElement?): PsiElement? {
        if (element == null) return null
        return PsiTreeUtil.getParentOfType(element, PsiClass::class.java)
    }

    override fun insertCommentBeforeElement(
        project: Project,
        element: PsiElement,
        comment: String
    ) {
        val factory = JavaPsiFacade.getInstance(project).elementFactory

        WriteCommandAction.runWriteCommandAction(project) {
            val commentElement = factory.createDocCommentFromText(comment, element)
            val parent = element.parent
            if (element.containingFile != null && parent != null) {
                parent.addBefore(commentElement, element)
            }
        }
    }


    override fun createCommentElement(project: Project, element: PsiElement): PsiElement {
        TODO("Not yet implemented")
    }

    override fun addTextChunkToComment(
        project: Project,
        comment: PsiElement,
        text: String
    ) {
        TODO("Not yet implemented")
    }

    override fun analyzePsiMethod(element: PsiElement): MethodStructure? {
        if (element !is PsiMethod) return null

        return ApplicationManager.getApplication().runReadAction<MethodStructure> {
            MethodStructure(
                code = element.text,
                language = element.language.displayName,
                complexity = "Complexity",
                paramNames = element.parameterList.parameters.map { it.name },
                hasReturnValue = element.returnType != null,
                exceptionNames = element.throwsList.referenceElements.map { it.text },
            )
        }
    }

    override fun analyzePsiClass(element: PsiElement): MethodStructure? {
        if (element !is PsiClass) return null

        return ApplicationManager.getApplication().runReadAction<MethodStructure> {
            MethodStructure(
                code = element.text,
                language = element.language.displayName,
                complexity = "Complexity",
                paramNames = emptyList(),
                hasReturnValue = false,
                exceptionNames = emptyList(),
            )
        }
        //TODO: Implement
    }

    override fun replaceCommentText(
        project: Project,
        element: PsiElement,
        text: String
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteElementComment(
        project: Project,
        element: PsiElement
    ) {
        TODO("Not yet implemented")
    }
}
