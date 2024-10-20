package com.github.ninz9.ideaplugin.actions

import com.github.ninz9.ideaplugin.generators.GeneratorImpl
import com.github.ninz9.ideaplugin.psi.LangManipulatorFactory
import com.github.ninz9.ideaplugin.utils.getEditor
import com.github.ninz9.ideaplugin.utils.getFile
import com.github.ninz9.ideaplugin.utils.renderValidCommentGradually
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * An action to create a comment for a method where the caret is currently positioned.
 * This action is triggered within an IDE, typically from a context menu
 */
class CreateCommentForMethodStream : AnAction() {

    /**
     * Handles the action event and performs the necessary operations to generate and render a KDoc comment
     * for the method at the current caret position in the editor.
     *
     * @param event The action event providing context such as the current editor, project, and file.
     */
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val project = event.project ?: return
        val file = event.getFile() ?: return


        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
        val method = psiManipulator.getCaretMethod(editor.caretModel.offset, file) ?: return
        val codeStructure = psiManipulator.analyzePsiMethod(method) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val comment = service<GeneratorImpl>().generateCommentForFunctionStream(codeStructure)
            renderValidCommentGradually(psiManipulator, comment, project, method, codeStructure)
        }
    }

    /**
     * Updates the action's presentation based on the context of the given event.
     *
     * @param event The event representing the action's context, from which the current editor and file are obtained.
     */
    override fun update(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val file = event.getFile() ?: return

        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
        val method = psiManipulator.getCaretMethod(editor.caretModel.offset, file)
        event.presentation.isEnabledAndVisible = method != null
    }

    /**
     * Specifies the type of thread the action should use for updates.
     *
     * @return The thread type for action updates. This implementation returns `ActionUpdateThread.BGT`, indicating the
     * action should be updated in the background thread.
     */
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}