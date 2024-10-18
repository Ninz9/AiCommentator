package com.github.ninz9.ideaplugin.utils

import com.github.ninz9.ideaplugin.formatters.FormatterFactory
import com.github.ninz9.ideaplugin.psi.PsiManipulator
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
import kotlin.time.Duration.Companion.milliseconds

/**
 * Gradually renders a valid comment for the given PSI element by processing chunks of comments from a flow.
 *
 * @param psiManipulator The PSI manipulator responsible for handling PSI operations.
 * @param commentFlow A flow of strings representing chunks of the comment text.
 * @param project The current project context.
 * @param element The PSI element before which the comment will be inserted.
 * @param language The programming language for which the comment formatting is needed.
 */
@OptIn(FlowPreview::class, kotlin.time.ExperimentalTime::class)
suspend fun renderValidCommentGradually(
    psiManipulator: PsiManipulator,
    commentFlow: Flow<String>,
    project: Project,
    element: PsiElement,
    language: String
) {
    var accumulatedComment = ""
    val formatter = service<FormatterFactory>().getFormatter(language)

    commentFlow
        .onEach { chunk ->
            accumulatedComment += chunk
        }
        .buffer(capacity = Channel.UNLIMITED)
        .sample(100.milliseconds)
        .onEach {
            val formattedComment = formatter.formatDoc(accumulatedComment)
            withContext(Dispatchers.Main) {
                psiManipulator.insertCommentBeforeElement(project, element, formattedComment)
            }
        }
        .catch { e ->
            println("Error in comment processing: ${e.message}")
        }
        .onCompletion {
            val finalComment = formatter.formatDoc(accumulatedComment)
            withContext(Dispatchers.Main) {
                psiManipulator.insertCommentBeforeElement(project, element, finalComment)
            }
        }
        .flowOn(Dispatchers.Default)
        .collect()
}