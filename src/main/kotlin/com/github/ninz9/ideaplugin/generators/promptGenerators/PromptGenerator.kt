package com.github.ninz9.ideaplugin.generators.promptGenerators

import com.github.ninz9.ideaplugin.utils.types.MethodStructure
import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.intellij.openapi.components.Service

@Service
class PromptGenerator {

    private val typeStreamPrompt = "Under no circumstances should you include the comment start and end characters (e.g. /** and */ for Java and Kotlin), or the newline character at the end."

    fun generatePromptForMethod(methodStructure: MethodStructure): Collection<ModelMessage> {

        val rolePrompt =
            "You are acting as an assistant to generate comments for class methods. Your task is to create a brief and precise comment for the provided method in JavaDoc format. The comment should explain the method’s purpose, key input parameters, and return value if applicable. The response should contain only the comment text, without any additional formatting such as code blocks, backticks, or extra characters. This comment will be automatically inserted into the code, so avoid additional explanations or details."

        return listOf(
            ModelMessage(
                "assistant",
                rolePrompt
            ),
            ModelMessage(
                "assistant",
                typeStreamPrompt
            ),
            ModelMessage(
                "user",
                methodStructure.code
            )
        )
    }


    fun generatePromptForClass(methodStructure: MethodStructure): Collection<ModelMessage> {

        val rolePrompt =
            "You are acting as an assistant to generate comments for classes. Your task is to create a brief and precise comment for the provided class in JavaDoc format. The comment should explain the class’s purpose, properties, key attributes, and any other relevant information. The response should contain only the comment and nothing else, without any additional formatting such as code blocks, backticks, or extra characters. This comment will be automatically inserted into the code, so avoid additional explanations or details."

        return listOf(
            ModelMessage(
                "assistant",
                rolePrompt
            ),
            ModelMessage(
                "assistant",
                typeStreamPrompt
            ),
            ModelMessage(
                "user",
                methodStructure.code
            )
        )
    }
}
