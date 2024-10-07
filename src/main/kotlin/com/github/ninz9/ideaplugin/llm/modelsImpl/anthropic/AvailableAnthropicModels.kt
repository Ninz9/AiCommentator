package com.github.ninz9.ideaplugin.llm.modelsImpl.anthropic

enum class AvailableAnthropicModels(val modelName: String) {
    sonnet35("claude-3-5-sonnet-20240620"),
    opus3("claude-3-opus-20240229"),
    sonnet3("claude-3-sonnet-20240229"),
    haiku3("claude-3-haiku-20240307"),
}
