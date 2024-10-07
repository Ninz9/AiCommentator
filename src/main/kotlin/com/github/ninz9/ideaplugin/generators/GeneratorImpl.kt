package com.github.ninz9.ideaplugin.generators

import com.github.ninz9.ideaplugin.llm.ModelFactoryWithSimplePromptGenerator
import com.github.ninz9.ideaplugin.generators.promptGenerators.PromptGenerator
import com.github.ninz9.ideaplugin.utils.readText
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import kotlinx.coroutines.flow.Flow

@Service
class GeneratorImpl : Generator {

    override suspend fun generateCommentForFunction(element: PsiElement): String {
        val model = ModelFactory().getModel()
        val codeStructure = analyzeElement(element)
        val messages = service<PromptGenerator>().generatePromptForMethod(codeStructure)

       val res = model.sendRequest(messages)
        return res
    }

    override suspend fun generateCommentForClass(element: PsiElement): String {
        val codeStructure = analyzeElement(element)
        val messages = service<PromptGenerator>().generatePromptForClass(codeStructure)
        val model = ModelFactory().getModel()

        return model.sendRequest(messages)
    }

    override suspend fun generateCommentForFunctionStream(element: PsiElement): Flow<String> {
        val codeStructure = analyzeElement(element)
        val messages = service<PromptGenerator>().generatePromptForMethod(codeStructure)
        val model = ModelFactory().getModel()

        return model.sendRequestStream(messages)
    }

    override suspend fun generateCommentForClassStream(element: PsiElement): Flow<String> {
        val codeStructure = analyzeElement(element)
        val messages = service<PromptGenerator>().generatePromptForClass(codeStructure)
        val model = ModelFactory().getModel()

        return model.sendRequestStream(messages)
    }

    private fun analyzeElement(element: PsiElement): CodeStructure {

        val text = element.readText()
        val codeStructure = CodeStructure(
            code = text,
            element.language.displayName,
            calculateComplexity(text)
        )
        return codeStructure
    }

    private fun calculateComplexity(code: String): String {
        return "Complexity"
    }
}
