package com.github.ninz9.ideaplugin.generators.promptGenerators

import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.intellij.openapi.components.Service

@Service
class PromptGenerator {

    private val typeStreamPrompt = "Your task is to generate code comments in KDoc, JavaDoc format. You will be provided with code for a method or a class." +
            " For methods, generate a comment that includes:\n" +
            "- A brief description of the method's purpose\n" +
            "- @param for each parameter (if any)\n" +
            "- @return with a description of the return value (if the method returns something)\n" +
            "- @throws with a description of exceptions (if the method can throw any)\n" +
            "For classes, generate a comment that includes:\n" +
            "- A brief description of the class's purpose\n" +
            "- @property for each class property\n" +
            "- @constructor for the constructor (if present)\n" +
            "Output only the ready-to-use comment without any additional explanations or surrounding text. \n" +
            "Do not include any code snippets in the comment, and do not include any annotations other than the ones mentioned above."

    fun generatePromptForMethod(codeStructure: CodeStructure): Collection<ModelMessage> {

        return listOf(
            ModelMessage(
                "assistant",
                typeStreamPrompt
            ),
            ModelMessage(
                "user",
                codeStructure.code
            )
        )
    }
    fun generatePromptForClass(codeStructure: CodeStructure): Collection<ModelMessage> {

        return listOf(
            ModelMessage(
                "assistant",
                typeStreamPrompt
            ),
            ModelMessage(
                "user",
                codeStructure.code
            )
        )
    }
}
