package com.github.ninz9.ideaplugin.utils

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.github.ninz9.ideaplugin.formatters.FormatterFactory
import com.github.ninz9.ideaplugin.psi.PsiManipulator
import com.github.ninz9.ideaplugin.utils.exeptions.AiCommentatorException
import com.github.ninz9.ideaplugin.utils.exeptions.handleAiCommenterError
import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds


/**
 * Renders a valid comment gradually in an asynchronous manner by consuming strings from a provided flow,
 * formatting them, and then inserting them before a specified PSI element within an IDE project.
 *
 * @param psiManipulator The `PsiManipulator` that handles PSI element manipulations.
 * @param commentFlow The `Flow` of strings representing chunks of the comment to be rendered and inserted.
 * @param project The current `Project` context in which the PSI element exists.
 * @param element The `PsiElement` before which the generated comment will be inserted.
 * @param codeStructure The `CodeStructure` representing the structure of the code related to the comment.
 */
@OptIn(FlowPreview::class, kotlin.time.ExperimentalTime::class)
suspend fun renderValidCommentGradually(
    psiManipulator: PsiManipulator,
    commentFlow: Flow<String>,
    project: Project,
    element: PsiElement,
    codeStructure: CodeStructure
) {
    var accumulatedComment = ""
    val formatter = service<FormatterFactory>().getFormatter(codeStructure.language)
    val transactionId = UUID.randomUUID().toString()

    commentFlow
        .onEach { chunk ->
            accumulatedComment += chunk
        }
        .buffer(capacity = Channel.UNLIMITED)
        .sample(100.milliseconds)
        .onEach {
            val formattedComment = formatter.formatDoc(accumulatedComment)
            withContext(Dispatchers.Main) {
                psiManipulator.insertCommentBeforeElement(project, element, formattedComment, transactionId)
            }
        }
        .catch { e ->
           if (e is AiCommentatorException) {
               handleAiCommenterError(e, project)
           }
        }
        .onCompletion {
            if (accumulatedComment.trim().isBlank()) return@onCompletion
            val finalComment = formatter.formatDoc(accumulatedComment)
             if (!formatter.isValidDoc(finalComment, codeStructure)) {
                service<NotificationsUtil>().showWarning(AiCommentatorBundle.message("warning.message.incomplete_comment"), project)
            }
            withContext(Dispatchers.Main) {
                psiManipulator.insertCommentBeforeElement(project, element, finalComment, transactionId)
            }
        }
        .flowOn(Dispatchers.IO)
        .collect()
}