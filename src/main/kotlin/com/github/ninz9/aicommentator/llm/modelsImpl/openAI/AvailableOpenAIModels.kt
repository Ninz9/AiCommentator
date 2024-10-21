package com.github.ninz9.aicommentator.llm.modelsImpl.openAI

/**
 * Enum class representing the available models provided by OpenAI.
 *
 * @property modelName The name of the model as recognized by OpenAI's API.
 */
enum class AvailableOpenAIModels(val modelName: String) {
    Gpt4oMini("gpt-4o-mini"),
    Gpt4o("gpt-4o"),
    Gpt4oXL("gpt-4o-XL")
}
