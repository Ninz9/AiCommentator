package com.github.ninz9.ideaplugin.psi

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service


@Service
class LangManipulatorFactory  {

    fun getLangManipulator(event: AnActionEvent): PsiManipulator {
        val lang = event.getData(CommonDataKeys.PSI_FILE)?.language?.displayName ?: throw IllegalArgumentException("Language not found")

        return when (lang) {
            "Java" -> service<JavaLangPsiManipulator>()
            "Kotlin" -> service<KotlinLangPsiManipulator>()
            "Go" -> service<GoLangPsiManipulator>()
            else -> throw IllegalArgumentException("Unsupported language")
        }
    }
}
