package com.github.ninz9.ideaplugin.actions

import com.github.ninz9.ideaplugin.generators.GeneratorImpl
import com.github.ninz9.ideaplugin.psi.LangManipulatorFactory
import com.github.ninz9.ideaplugin.psi.PsiManipulator
import com.github.ninz9.ideaplugin.utils.getEditor
import com.github.ninz9.ideaplugin.utils.getFile
import com.github.ninz9.ideaplugin.utils.readText
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class CreateCommentForClassStream : AnAction() {


    @OptIn(FlowPreview::class)
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val project = event.project ?: return
        val file = event.getFile() ?: return


        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)

        val clazz = psiManipulator.getCaretClass(editor.caretModel.offset, file) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val comment = service<GeneratorImpl>().generateCommentForClassStream(clazz)
            val commentElement = psiManipulator.createCommentElement(project, clazz) ?: return@launch
            withContext(Dispatchers.Main) {
                psiManipulator.insertCommentBeforeElement(project, clazz, commentElement.readText())
            }
            comment.let {
                renderValidCommentGradually(psiManipulator, it, project, commentElement)
            }
        }
    }

    override fun update(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val file = event.getFile() ?: return
        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
        val method = psiManipulator.getCaretClass(editor.caretModel.offset, file)
        event.presentation.isEnabledAndVisible = method != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }



    /**
     * Formats a partial comment by breaking it into lines of a specified length
     * and optionally adding the end of the comment.
     *
     * @param comment The comment string to be formatted.
     * @param isFinal A flag indicating whether this is the final part of the comment.
     *                If true, the closing comment tag will be added.
     * @return The formatted comment string.
     */
    private fun formatPartialComment(comment: String, isFinal: Boolean = false): String {
        val commentStart = "/**"
        val commentEnd = " */"
        val lineStart = " * "

        val lines = comment.split(Regex("\\s+"))
        var result = commentStart + "\n"
        var currentLine = lineStart

        for (word in lines) {
            if (currentLine.length + word.length > 80) {  // Предполагаем максимальную длину строки 80 символов
                result += currentLine + "\n"
                currentLine = lineStart
            }
            currentLine += word + " "
        }

        result += currentLine.trimEnd() + "\n"

        if (isFinal) {
            result += commentEnd
        } else {
            result += " *" // Добавляем незакрытую звездочку для визуального продолжения
        }

        return result
    }

    private suspend fun renderValidCommentGradually(psiManipulator: PsiManipulator, commentFlow: Flow<String>, project: Project, commentElement: PsiElement) {
        var accumulatedComment = ""
        val commentStart = "/**"
        val commentEnd = " */"
        val lineStart = " * "

        commentFlow
            .onEach { chunk ->
                // Очищаем чанк от символов начала/конца комментария и лишних пробелов
                val cleanChunk = chunk.trim()
                    .removePrefix(commentStart)
                    .removeSuffix(commentEnd)
                    .trim()
                    .replace(Regex("^\\s*\\*\\s*"), "") // Удаляем начало строки комментария

                // Добавляем очищенный чанк к накопленному комментарию
                if (accumulatedComment.isEmpty()) {
                    accumulatedComment = cleanChunk
                } else {
                    accumulatedComment += " " + cleanChunk
                }

                // Форматируем накопленный комментарий
                val formattedComment = formatPartialComment(accumulatedComment)

                // Обновляем комментарий в UI
                withContext(Dispatchers.Main) {
                    psiManipulator.addTextChunkToComment(project, commentElement, formattedComment)
                }
            }
            .buffer(capacity = Channel.UNLIMITED)
            .catch { e ->
                println("Error in comment processing: ${e.message}")
            }
            .onCompletion {
                // Финальное обновление комментария
                val finalComment = formatPartialComment(accumulatedComment, isFinal = true)
                withContext(Dispatchers.Main) {
                    psiManipulator.insertCommentBeforeElement(project, commentElement, finalComment)
                }
            }
            .flowOn(Dispatchers.Default)
            .collect()
    }
}
