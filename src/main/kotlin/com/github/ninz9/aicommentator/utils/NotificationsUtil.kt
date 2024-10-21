package com.github.ninz9.aicommentator.utils

import com.github.ninz9.aicommentator.utils.exeptions.AiCommentatorException
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * Utility class for displaying various types of notifications within an IDE context.
 */
@Service()
class NotificationsUtil {

    private val notificationGroupName = "ai.commentator.notification"

    fun showInfo(message: String, project: Project) {
        service<NotificationGroupManager>().getNotificationGroup(notificationGroupName)
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project)
    }

    fun showError(error: AiCommentatorException, project: Project) {
        service<NotificationGroupManager>().getNotificationGroup(notificationGroupName)
            .createNotification(error.getDisplayMessage(), NotificationType.ERROR)
            .addActionIfNotNull(error.getNotificationAction(project))
            .notify(project)
    }

    fun showWarning(message: String, project: Project) {
        service<NotificationGroupManager>().getNotificationGroup(notificationGroupName)
            .createNotification(message, NotificationType.WARNING)
            .notify(project)
    }

    fun Notification.addActionIfNotNull(action: NotificationAction?): Notification {
        action?.let { addAction(it) }
        return this
    }
}
