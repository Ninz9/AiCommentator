package com.github.ninz9.aicommentator.actions

import com.github.ninz9.aicommentator.generators.GeneratorImpl
import com.github.ninz9.aicommentator.psi.LangManipulatorFactory
import com.github.ninz9.aicommentator.utils.getEditor
import com.github.ninz9.aicommentator.utils.getFile
import com.github.ninz9.aicommentator.utils.renderValidCommentGradually
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import kotlinx.coroutines.*


class CreateCommentForClassStream : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val project = event.project ?: return
        val file = event.getFile() ?: return

        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
        val clazz = psiManipulator.getCaretClass(editor.caretModel.offset, file) ?: return
        val codeStructure = psiManipulator.analyzePsiClass(clazz) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val comment = service<GeneratorImpl>().generateCommentForClassStream(codeStructure)
            renderValidCommentGradually(psiManipulator, comment, project, clazz, codeStructure)
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
}
