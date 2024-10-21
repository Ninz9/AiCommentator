package com.github.ninz9.aicommentator.utils.exeptions

import com.github.ninz9.aicommentator.AiCommentatorBundle
import com.github.ninz9.aicommentator.configuration.modelConfigurations.anthropic.AnthropicConfigurable
import com.github.ninz9.aicommentator.configuration.modelConfigurations.openAI.OpenAIConfigurable
import com.github.ninz9.aicommentator.llm.AiModel
import com.intellij.notification.NotificationAction
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

sealed class AiCommentatorException(
    val aiModel: AiModel,
    override val message: String
) : Exception(message) {

    class InvalidToken(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.invalid_token")
    )

    class RateLimitError(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.rate_limit_exceeded")
    )

    class ServerError(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.server_error")
    )

    class OverloadedError(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.overloaded_error")
    )

    class EmptyMessage(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.empty_message")
    )

    class RequestTooLarge(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.request_too_large")
    )

    class InsufficientQuota(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.insufficient_quota")
    )

    class TimeoutError(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.timeout")
    )

    class ServiceUnavailable(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.service_unavailable")
    )

    class PermissionDenied(aiModel: AiModel) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.permission_denied")
    )

    class UnknownError(aiModel: AiModel, message: String) : AiCommentatorException(
        aiModel,
        AiCommentatorBundle.message("error.message.unknown_error") + message
    )

    fun getDisplayMessage(): String {
        return "<div><b>Error occurred with ${aiModel.displayedName} model.</b></div> $message"
    }

    fun getNotificationAction(project: Project): NotificationAction? {
        if (this is InvalidToken) {
            val configurable = when (aiModel) {
                AiModel.Anthropic -> AnthropicConfigurable::class.java
                AiModel.OpenAI -> OpenAIConfigurable::class.java
            }

            return NotificationAction.createSimple(AiCommentatorBundle.message("notification.invalid_token.button.label")) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, configurable)
            }
        }
        return null
    }
}