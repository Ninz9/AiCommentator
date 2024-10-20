package com.github.ninz9.ideaplugin.utils.exeptions

import com.github.ninz9.ideaplugin.utils.NotificationsUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

fun handleAiCommenterError(aiCommentatorException: AiCommentatorException, project: Project) {
    service<NotificationsUtil>().showError(aiCommentatorException, project)
}