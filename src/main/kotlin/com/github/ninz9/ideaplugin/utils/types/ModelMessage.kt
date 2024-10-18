package com.github.ninz9.ideaplugin.utils.types

/**
 * Data class representing a message model used to interact with Language Learning Model (LLM) services.
 *
 * @property role The role associated with the message, typically specifying who is the sender of the message (e.g., system, user, assistant).
 * @property message The content of the message to be processed by the LLM service.
 */
data class ModelMessage(
    val role: String,
    val message: String
)
