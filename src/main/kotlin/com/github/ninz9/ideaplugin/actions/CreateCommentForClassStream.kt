//package com.github.ninz9.ideaplugin.actions
//
//import com.github.ninz9.ideaplugin.generators.GeneratorImpl
//import com.github.ninz9.ideaplugin.psi.LangManipulatorFactory
//import com.intellij.openapi.actionSystem.ActionUpdateThread
//import com.intellij.openapi.actionSystem.AnAction
//import com.intellij.openapi.actionSystem.AnActionEvent
//import com.intellij.openapi.application.EDT
//import com.intellij.openapi.components.service
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.buffer
//import kotlinx.coroutines.flow.debounce
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.launch
//
//class CreateCommentForClassStream: AnAction() {
//
//    override fun actionPerformed(event: AnActionEvent) {
//        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
//
//        val clazz = psiManipulator.getCaretClass(event) ?: return
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val comment = service<GeneratorImpl>().generateCommentForClassStream(clazz)
//            psiManipulator.createCommentElement(event)?.let { commentElement ->
//               comment.debounce(200).buffer().collect {
//                   println(it)
//                   psiManipulator.addTextChunkToComment(event, commentElement, it)
//               }
//            }
//        }
//    }
//
//    override fun update(event: AnActionEvent) {
//        val psiManipulator = service<LangManipulatorFactory>().getLangManipulator(event)
//        val clazz = psiManipulator.getCaretClass(event)
//        event.presentation.isEnabledAndVisible = clazz != null
//    }
//
//    override fun getActionUpdateThread(): ActionUpdateThread {
//        return ActionUpdateThread.BGT
//    }
//}
