package com.github.ninz9.ideaplugin.generators.promptGenerators

import com.github.ninz9.ideaplugin.generators.CodeStructure
import com.github.ninz9.ideaplugin.generators.ModelMessage
import com.intellij.openapi.components.Service

@Service
class PromptGenerator {

    val typePrompt = "Create a comment in JavaDoc format for the provided code snippet."

    fun generatePromptForMethod(codeStructure: CodeStructure): Collection<ModelMessage> {

        val rolePrompt =
            "You are acting as an assistant to generate comments for class methods. Your task is to create a brief and precise comment for the provided method. The comment should explain the method’s purpose, key input parameters, and return value if applicable. The response should contain only the comment and nothing else. This comment will be automatically inserted into the code, so avoid additional explanations or details."

        return listOf(
            ModelMessage(
                "assistant",
                typePrompt
            ),
            ModelMessage(
                "assistant",
                rolePrompt
            ),
            ModelMessage(
                "user",
                codeStructure.code
            )
        )
    }


    fun generatePromptForClass(codeStructure: CodeStructure): Collection<ModelMessage> {

        val rolePrompt =
            "You are acting as an assistant to generate comments for classes. Your task is to create a brief and precise comment for the provided class. The comment should explain the class’s purpose, key attributes, and any other relevant information. The response should contain only the comment and nothing else. This comment will be automatically inserted into the code, so avoid additional explanations or details."

        return listOf(
            ModelMessage(
                "assistant",
                typePrompt
            ),
            ModelMessage(
                "assistant",
                rolePrompt
            ),
            ModelMessage(
                "user",
                codeStructure.code
            )
        )
    }
}
