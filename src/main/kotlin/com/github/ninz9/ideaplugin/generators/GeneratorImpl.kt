package com.github.ninz9.ideaplugin.generators

import com.github.ninz9.ideaplugin.llm.ModelFactory
import com.github.ninz9.ideaplugin.generators.promptGenerators.PromptGenerator
import com.github.ninz9.ideaplugin.utils.types.MethodStructure
import com.github.ninz9.ideaplugin.formatters.FormatterFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.flow.Flow

@Service
class GeneratorImpl : Generator {

    override suspend fun generateCommentForFunction(element: MethodStructure): String {
        val model = ModelFactory().getModel()
        val messages = service<PromptGenerator>().generatePromptForMethod(element)

        var res = model.sendRequest(messages)

        while (isDocInvalid(element, res)) {
            res = model.sendRequest(messages)
            println("Invalid doc: $res")
        }
        return res
    }

    override suspend fun generateCommentForClass(element: MethodStructure): String {
        val messages = service<PromptGenerator>().generatePromptForClass(element)
        val model = ModelFactory().getModel()

        return model.sendRequest(messages)
    }

    override suspend fun generateCommentForFunctionStream(element: MethodStructure): Flow<String> {
        val messages = service<PromptGenerator>().generatePromptForMethod(element)
        val model = ModelFactory().getModel()

        return model.sendRequestStream(messages)
    }

    override suspend fun generateCommentForClassStream(element: MethodStructure): Flow<String> {
        val messages = service<PromptGenerator>().generatePromptForClass(element)
        val model = ModelFactory().getModel()

        return model.sendRequestStream(messages)
    }

    private fun isDocInvalid(methodStructure: MethodStructure, doc: String): Boolean {
        val validator = FormatterFactory().getFormatter(methodStructure.language)
        return !validator.isValidDoc(doc,
           // methodStructure.paramNames, methodStructure.hasReturnValue, methodStructure.exceptionNames
        )
    }
}
