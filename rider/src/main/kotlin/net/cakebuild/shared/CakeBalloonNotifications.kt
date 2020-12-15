package net.cakebuild.shared

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object CakeBalloonNotifications {
    // after 2020.03 this can be done in pluxin.xml, see https://jetbrains.org/intellij/sdk/docs/user_interface_components/notifications.html
    private val notificationGroup =
        NotificationGroup("Cake", NotificationDisplayType.BALLOON, true)

    private fun notify(project: Project, content: String, type: NotificationType) {
        notificationGroup.createNotification(content, type).notify(project)
    }

    fun notifyError(project: Project, content: String) {
        notify(project, content, NotificationType.ERROR)
    }
}
