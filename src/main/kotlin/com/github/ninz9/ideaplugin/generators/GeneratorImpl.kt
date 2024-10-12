package com.github.ninz9.ideaplugin.generators

import com.github.ninz9.ideaplugin.llm.ModelFactory
import com.github.ninz9.ideaplugin.generators.promptGenerators.PromptGenerator
import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.github.ninz9.ideaplugin.formatters.FormatterFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.flow.Flow

@Service
class GeneratorImpl : Generator {

    override suspend fun generateCommentForFunction(element: CodeStructure): String {
        val model = ModelFactory().getModel()
        val messages = service<PromptGenerator>().generatePromptForMethod(element)

        var res = model.sendRequest(messages)


        var retryCount = 1
        while (isDocInvalid(element, res)) {
            if (retryCount == 0) {
                println("invalid doc: $res")
                throw Exception("Failed to generate a valid comment")
            }
            res = model.sendRequest(messages)
            retryCount--
        }
        return res
    }

    override suspend fun generateCommentForClass(element: CodeStructure): String {
        val messages = service<PromptGenerator>().generatePromptForClass(element)
        val model = ModelFactory().getModel()

        return model.sendRequest(messages)
    }

    override suspend fun generateCommentForFunctionStream(element: CodeStructure): Flow<String> {
        val messages = service<PromptGenerator>().generatePromptForMethod(element)
        val model = ModelFactory().getModel()

        return model.sendRequestStream(messages)
    }

    override suspend fun generateCommentForClassStream(element: CodeStructure): Flow<String> {
        val messages = service<PromptGenerator>().generatePromptForClass(element)
        val model = ModelFactory().getModel()

        return model.sendRequestStream(messages)
    }

    private fun isDocInvalid(codeStructure: CodeStructure, doc: String): Boolean {
        val validator = FormatterFactory().getFormatter(codeStructure.language)
        return !validator.isValidDoc(
            doc,
            codeStructure.paramNames, codeStructure.hasReturnValue, codeStructure.exceptionNames
        )
    }
}
