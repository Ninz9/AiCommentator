package com.github.ninz9.ideaplugin.generators.promptGenerators

import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import com.github.ninz9.ideaplugin.utils.types.ModelMessage
import com.intellij.openapi.components.Service

/**
 * Service class for generating prompt messages to create code comments.
 * This class facilitates the generation of documentation comments for methods and classes based on the given code structure.
 */
@Service
class PromptGenerator {


    private val methodCommentPrompt = """
                    Your task is to generate code comments for a given method. You will be provided with the method code.
                    
                    
                    Output only the ready-to-use comment without any additional explanations or surrounding text.
                    Do not include any code snippets in the comment, and do not include any annotations other than the ones mentioned above."""

    private val classCommentPrompt = """
                    Your task is to generate code comments for a given class. You will be provided with the class code.

                    Output only the ready-to-use comment without any additional explanations or surrounding text.
                    Do not include any code snippets in the comment, and do not include any annotations other than the ones mentioned above.
                    Do not describe methods inside the class."""

    private val javaCommentPrompt = """
                Do not return example code, do not use @author or @version or @since tags.
                DO NOT generate example usage.
                DO NOT generate usage example.
                DO NOT use html tags such as <p>, <lu>, <li>.
                DO NOT generate documentation for type member properties.
                Write javadoc."""

    private val kotlinCommentPrompt = """
                Do not return example code, do not use @author or @version or @since tags.
                DO NOT generate example usage.
                DO NOT generate usage example.
                DO NOT use html tags such as <p>, <lu>, <li>.
                DO NOT generate documentation for type member properties.
                Write KDoc."""

    fun generatePromptForMethod(codeStructure: CodeStructure): Collection<ModelMessage> {

        return listOf(
            getCommentPromptForLanguage(codeStructure.language),
            ModelMessage(
                "assistant", methodCommentPrompt
            ), ModelMessage(
                "user", codeStructure.code
            )
        )
    }

    fun generatePromptForClass(codeStructure: CodeStructure): Collection<ModelMessage> {
        return listOf(
            getCommentPromptForLanguage(codeStructure.language),
            ModelMessage(
                "assistant", classCommentPrompt
            ), ModelMessage(
                "user", codeStructure.code
            )
        )
    }


    private fun getCommentPromptForLanguage(language: String): ModelMessage {
        return when (language) {
            "Java" -> ModelMessage("assistant", javaCommentPrompt)
            "Kotlin" -> ModelMessage("assistant", kotlinCommentPrompt)
            else -> ModelMessage("assistant", "")
        }
    }
}
