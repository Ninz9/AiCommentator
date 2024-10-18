package com.github.ninz9.ideaplugin.psi

import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil


/**
 * JavaLangPsiManipulator is a service class that provides various manipulations
 * and analyses on Java PSI elements.
 */
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
                deleteElementComment(project, element)
                parent.addBefore(commentElement, element)
            }
        }
    }

    override fun analyzePsiMethod(element: PsiElement): CodeStructure? {
        if (element !is PsiMethod) return null

        return ApplicationManager.getApplication().runReadAction<CodeStructure> {
            CodeStructure(
                code = element.text,
                language = element.language.displayName,
                paramNames = element.parameterList.parameters.map { it.name },
                hasReturnValue = element.returnType != null,
                exceptionNames = element.throwsList.referenceElements.map { it.text },
            )
        }
    }

    override fun analyzePsiClass(element: PsiElement): CodeStructure? {
        if (element !is PsiClass) return null

        return ApplicationManager.getApplication().runReadAction<CodeStructure> {
            CodeStructure(
                code = element.text,
                language = element.language.displayName,
                hasReturnValue = false,
                paramNames = element.fields.map { it.name },
            )
        }
    }
}
