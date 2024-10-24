package com.github.ninz9.aicommentator.psi

import com.github.ninz9.aicommentator.utils.generateTransactionId
import com.github.ninz9.aicommentator.utils.types.CodeStructure
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil

/**
 * Interface that defines methods for manipulating PSI (Program Structure Interface) elements within an IDE context.
 */
interface PsiManipulator {

    /**
     * Retrieves the parent method of the element at the specified offset within the given PSI file.
     *
     * @param offset The offset within the file where the caret is located.
     * @param file The PSI file in which to look for the parent method.
     * @return The parent method of the element at the specified offset, or null if no parent method is found.
     */
    fun getCaretMethod(offset: Int, file: PsiElement): PsiElement? {
        return findParentMethod(file.findElementAt(offset))
    }

    /**
     * Retrieves the parent class of the element at the specified offset within the given PSI file.
     *
     * @param offset The offset within the file where the caret is located.
     * @param file The PSI file in which to look for the parent class.
     * @return The parent class of the element at the specified offset, or null if no parent class is found.
     */
    fun getCaretClass(offset: Int, file: PsiElement): PsiElement? {
        return findParentClass(file.findElementAt(offset))
    }

    /**
     * Finds the parent method of the given PSI element in the code structure.
     *
     * @param element The PSI element for which the parent method is to be located. It may be null, in which case the method will return null.
     * @return The parent method of the given PSI element, or null if the parent method cannot be determined or if the input element is null.
     */
    fun findParentMethod(element: PsiElement?): PsiElement?

    /**
     * Finds the parent class of the given PSI element in the code structure.
     *
     * @param element The PSI element for which the parent class is to be located. It may be null, in which case the method will return null.
     * @return The parent class of the given PSI element, or null if the parent class cannot be determined or if the input element is null.
     */
    fun findParentClass(element: PsiElement?): PsiElement?

    /**
     * Inserts a comment before the specified PSI element within a given project.
     *
     * @param project The current project context.
     * @param element The PSI element before which the comment should be inserted.
     * @param comment The comment text to be inserted.
     * @param transactionId The transaction ID used to group multiple changes to the PSI tree into a single transaction, ensuring proper undo functionality.
     */
    fun insertCommentBeforeElement(project: Project, element: PsiElement, comment: String, transactionId: String = generateTransactionId())

    /**
     * Analyzes the given `PsiElement` representing a method and extracts its code structure.
     *
     * @param element The `PsiElement` to be analyzed, expected to represent a method within the code.
     * @return A `CodeStructure` object containing the details of the method, or `null` if the analysis fails.
     */
    fun analyzePsiMethod(element: PsiElement): CodeStructure?

    /**
     * Analyzes the given `PsiElement` representing a class and extracts its code structure.
     *
     * @param element The `PsiElement` to be analyzed, expected to represent a class within the code.
     * @return A `CodeStructure` object containing the details of the class, or `null` if the analysis fails.
     */
    fun analyzePsiClass(element: PsiElement): CodeStructure?

    /**
     * Deletes the comment associated with the specified PSI element within a given project.
     *
     * @param project The current project context.
     * @param element The PSI element whose associated comment should be deleted.
     */
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
                if (innerComment != null && innerComment.parent == element) {
                    innerComment.delete()
                }
            }
        }
    }
}
