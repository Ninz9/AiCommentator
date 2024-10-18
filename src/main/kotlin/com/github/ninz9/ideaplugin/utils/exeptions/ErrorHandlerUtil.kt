package com.github.ninz9.ideaplugin.utils.exeptions

import com.github.ninz9.ideaplugin.AiCommentatorBundle
import com.github.ninz9.ideaplugin.utils.NotificationsUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

fun handleAiCommenterError(aiCommentatorException: AiCommentatorException, project: Project) {
    val message = when (aiCommentatorException.errorType) {
        ErrorType.INVALID_TOKEN -> AiCommentatorBundle.message("error.message.invalid_token")
        ErrorType.RATE_LIMIT_ERROR -> AiCommentatorBundle.message("error.message.rate_limit_exceeded")
        ErrorType.SERVER_ERROR -> AiCommentatorBundle.message("error.message.server_error")
        ErrorType.OVERLOADED_ERROR -> AiCommentatorBundle.message("error.message.overloaded_error")
        ErrorType.EMPTY_MESSAGE -> AiCommentatorBundle.message("error.message.empty_message")
        ErrorType.REQUEST_TOO_LARGE -> AiCommentatorBundle.message("error.message.request_too_large")
        ErrorType.SERVICE_UNAVAILABLE -> AiCommentatorBundle.message("error.message.service_unavailable")
        ErrorType.INSUFFICIENT_QUOTA -> AiCommentatorBundle.message("error.message.insufficient_quota")
        ErrorType.PERMISSION_DENIED -> AiCommentatorBundle.message("error.message.permission_denied")
        ErrorType.UNKNOWN_ERROR -> AiCommentatorBundle.message("error.message.unknown_error")
        ErrorType.TIMEOUT_ERROR -> AiCommentatorBundle.message("error.message.timeout")
    }

    val modelInfo  = "Error occurred with ${aiCommentatorException.aiModel.displayedName} model.\n\n"

    service<NotificationsUtil>().showError(modelInfo + message, project)
}