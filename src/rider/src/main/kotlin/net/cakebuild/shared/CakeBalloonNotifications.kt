package net.cakebuild.shared

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object CakeBalloonNotifications {
    private fun notify(
        project: Project,
        content: String,
        type: NotificationType,
    ) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Cake")
            .createNotification(content, type)
            .notify(project)
    }

    fun notifyError(
        project: Project,
        content: String,
    ) {
        notify(project, content, NotificationType.ERROR)
    }

    fun notifyInformation(
        project: Project,
        content: String,
    ) {
        notify(project, content, NotificationType.INFORMATION)
    }

    fun notifyWarning(
        project: Project,
        content: String,
    ) {
        notify(project, content, NotificationType.WARNING)
    }
}
