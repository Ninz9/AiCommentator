package com.github.ninz9.ideaplugin.psi

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
            val commentElement = factory.createCommentFromText("/*\n$comment\n*/", element)
            val parent = element.parent
            if (element.containingFile != null && parent != null) {
                element.node.addChild(commentElement.node, element.firstChild.node)
                //reformat code
            }
        }
    }


    override fun createCommentElement(project: Project): PsiElement? {
        TODO("Not yet implemented")
    }

    override fun addTextChunkToComment(
        project: Project,
        comment: PsiElement,
        text: String
    ) {
        TODO("Not yet implemented")
    }
}
