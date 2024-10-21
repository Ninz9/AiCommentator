package com.github.ninz9.aicommentator.llm.modelsImpl.anthropic

/**
 * Enum class representing the available models from Anthropic's Claude series.
 *
 * Each enum constant corresponds to a specific version and configuration of the Claude model.
 *
 * @property modelName The string identifier for the Claude model.
 */
enum class AvailableAnthropicModels(val modelName: String) {
    Sonnet35("claude-3-5-sonnet-20240620"),
    Opus3("claude-3-opus-20240229"),
    Sonnet3("claude-3-sonnet-20240229"),
    Haiku3("claude-3-haiku-20240307"),
}
