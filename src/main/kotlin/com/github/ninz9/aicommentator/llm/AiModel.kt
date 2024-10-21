package com.github.ninz9.aicommentator.llm

/**
 * Represents different AI models with their display names.
 *
 * This enum class is used to specify the supported AI models
 * that the application can interact with, such as OpenAI and Anthropic.
 *
 * @property displayedName A human-readable name for the AI model.
 */
enum class AiModel(val displayedName: String) {
    OpenAI("OpenAI"),
    Anthropic("Anthropic"),
}
