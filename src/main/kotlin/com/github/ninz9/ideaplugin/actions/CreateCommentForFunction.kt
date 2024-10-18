package com.github.ninz9.ideaplugin.actions

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.github.ninz9.ideaplugin.generators.GeneratorImpl
import com.github.ninz9.ideaplugin.psi.LangManipulatorFactory
import com.github.ninz9.ideaplugin.utils.getEditor
import com.github.ninz9.ideaplugin.utils.getFile
import com.github.ninz9.ideaplugin.formatters.FormatterFactory
import com.github.ninz9.ideaplugin.utils.NotificationsUtil
import com.github.ninz9.ideaplugin.utils.exeptions.AiCommentatorException
import com.github.ninz9.ideaplugin.utils.exeptions.handleAiCommenterError
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateCommentForFunction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val project = event.project ?: return
        val file = event.getFile() ?: return

        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
        val method = psiManipulator.getCaretMethod(editor.caretModel.offset, file) ?: return
        val codeStructure = psiManipulator.analyzePsiMethod(method) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            var comment: String? = null
            try {
                comment = service<GeneratorImpl>().generateCommentForFunction(codeStructure)
            } catch (e: AiCommentatorException) {
                handleAiCommenterError(e, project)
                return@launch
            }
            val formatter = service<FormatterFactory>().getFormatter(codeStructure.language)
            comment = formatter.formatDoc(comment)

            if (formatter.isValidDoc(comment, codeStructure)){
                 service<NotificationsUtil>().showWarning(AiCommentatorBundle.message("warning.message.incomplete_comment"), project)
            }

            withContext(Dispatchers.Main) {
                psiManipulator.insertCommentBeforeElement(project, method, comment)
            }
        }
    }

    override fun update(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val file = event.getFile() ?: return

        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
        val method = psiManipulator.getCaretMethod(editor.caretModel.offset, file)
        event.presentation.isEnabledAndVisible = method != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
