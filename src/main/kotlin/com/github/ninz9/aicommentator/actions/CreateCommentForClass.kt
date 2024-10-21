package com.github.ninz9.aicommentator.actions

import com.github.ninz9.aicommentator.AiCommentatorBundle
import com.github.ninz9.aicommentator.formatters.FormatterFactory
import com.github.ninz9.aicommentator.generators.GeneratorImpl
import com.github.ninz9.aicommentator.psi.LangManipulatorFactory
import com.github.ninz9.aicommentator.utils.NotificationsUtil
import com.github.ninz9.aicommentator.utils.exeptions.AiCommentatorException
import com.github.ninz9.aicommentator.utils.exeptions.handleAiCommenterError
import com.github.ninz9.aicommentator.utils.getEditor
import com.github.ninz9.aicommentator.utils.getFile
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CreateCommentForClass : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getEditor() ?: return
        val project = event.project ?: return
        val file = event.getFile() ?: return

        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
        val clazz = psiManipulator.getCaretClass(editor.caretModel.offset, file) ?: return
        val codeStructure = psiManipulator.analyzePsiClass(clazz) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            var comment: String?
            try {
                comment = service<GeneratorImpl>().generateCommentForClass(codeStructure)
            } catch (e: AiCommentatorException) {
                handleAiCommenterError(e, project)
                return@launch
            }

            val codeFormatter = service<FormatterFactory>().getFormatter(codeStructure.language)
            comment = codeFormatter.formatDoc(comment)

            if (!codeFormatter.isValidDoc(comment, codeStructure)) {
                service<NotificationsUtil>().showWarning(AiCommentatorBundle.message("warning.message.incomplete_comment"), project)
            }

            withContext(Dispatchers.Main) {
                psiManipulator.insertCommentBeforeElement(project, clazz, comment, UUID.randomUUID().toString())
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
}
