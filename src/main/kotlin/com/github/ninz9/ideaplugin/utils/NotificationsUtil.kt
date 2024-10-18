package com.github.ninz9.ideaplugin.utils

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

    fun showError(message: String, project: Project) {
        service<NotificationGroupManager>().getNotificationGroup(notificationGroupName)
            .createNotification(message, NotificationType.ERROR)
            .notify(project)
    }

    fun showWarning(message: String, project: Project) {
        service<NotificationGroupManager>().getNotificationGroup(notificationGroupName)
            .createNotification(message, NotificationType.WARNING)
            .notify(project)
    }
}
