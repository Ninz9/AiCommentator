package com.github.ninz9.ideaplugin.generators

import com.github.ninz9.ideaplugin.llm.ModelFactory
import com.github.ninz9.ideaplugin.generators.promptGenerators.PromptGenerator
import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.github.ninz9.ideaplugin.formatters.FormatterFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of the Generator interface for generating KDoc comments.
 */
@Service
class GeneratorImpl {

    /**
     * Generates a KDoc comment for a given function's code structure.
     *
     * @param element The code structure of the function for which the comment is to be generated.
     * @return A KDoc comment string for the provided function.
     */
    suspend fun generateCommentForFunction(element: CodeStructure): String {
        val model = ModelFactory().getModel()
        val messages = service<PromptGenerator>().generatePromptForMethod(element)
        var res = model.sendRequest(messages)
        checkValidity(element, res)
        return res
    }

    /**
     * Generates a KDoc comment for a given class's code structure.
     *
     * @param element The code structure of the class for which the comment is to be generated.
     * @return A KDoc comment string for the provided class.
     */
    suspend fun generateCommentForClass(element: CodeStructure): String {
        val messages = service<PromptGenerator>().generatePromptForClass(element)
        val model = ModelFactory().getModel()
        val res = model.sendRequest(messages)
        checkValidity(element, res)
        return res
    }

    /**
     * Generates a KDoc comment stream for a given function's code structure.
     *
     * @param element The code structure of the function for which the comment stream is to be generated.
     * @return A Flow emitting strings, each representing a part of the KDoc comment for the provided function.
     */
    suspend fun generateCommentForFunctionStream(element: CodeStructure): Flow<String> {
        val model = ModelFactory().getModel()
        val messages = service<PromptGenerator>().generatePromptForMethod(element)

        return model.sendRequestStream(messages)
    }

    /**
     * Generates a KDoc comment stream for a given class's code structure.
     *
     * @param element The code structure of the class for which the comment stream is to be generated.
     * @return A Flow emitting strings, each representing a part of the KDoc comment for the provided class.
     */
    suspend fun generateCommentForClassStream(element: CodeStructure): Flow<String> {
        val messages = service<PromptGenerator>().generatePromptForClass(element)
        val model = ModelFactory().getModel()

        return model.sendRequestStream(messages)
    }

    /**
     * Validates the documentation string for a given code structure.
     *
     * @param codeStructure The structure of the code for which the documentation is being validated.
     * @param doc The documentation string that needs to be validated.
     */
    private fun checkValidity(codeStructure: CodeStructure, doc: String) {
        val validator = FormatterFactory().getFormatter(codeStructure.language)
        if (!validator.isValidDoc(
                doc, codeStructure.paramNames, codeStructure.hasReturnValue, codeStructure.exceptionNames
            )
        ) {
            throw IllegalArgumentException("Invalid documentation generated")
        }
    }
}
