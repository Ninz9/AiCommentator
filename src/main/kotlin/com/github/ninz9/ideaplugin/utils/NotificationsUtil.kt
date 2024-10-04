package com.github.ninz9.ideaplugin.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class NotificationsUtil {

    fun showInfo(message: String, project: Project) {
        project.service<NotificationGroupManager>().getNotificationGroup("IdeaPlugin")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project)
    }

    fun showError(message: String, project: Project) {
        project.service<NotificationGroupManager>().getNotificationGroup("IdeaPlugin")
            .createNotification(message, NotificationType.ERROR)
            .notify(project)
    }

    fun showWarning(message: String, project: Project) {
        project.service<NotificationGroupManager>().getNotificationGroup("IdeaPlugin")
            .createNotification(message, NotificationType.WARNING)
            .notify(project)
    }
}
