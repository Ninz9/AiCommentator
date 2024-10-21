package com.github.ninz9.aicommentator.psi

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service


/**
 * Factory service class responsible for providing the appropriate `PsiManipulator`
 * implementation based on the language of the file within the given `AnActionEvent`.
 */
@Service
class LangManipulatorFactory  {
    fun getLangManipulator(event: AnActionEvent): PsiManipulator {
        val lang = event.getData(CommonDataKeys.PSI_FILE)?.language?.displayName ?: throw IllegalArgumentException("Language not found")

        return when (lang) {
            "Java" -> service<JavaLangPsiManipulator>()
            "Kotlin" -> service<KotlinLangPsiManipulator>()
            else -> throw IllegalArgumentException("Unsupported language")
        }
    }
}
