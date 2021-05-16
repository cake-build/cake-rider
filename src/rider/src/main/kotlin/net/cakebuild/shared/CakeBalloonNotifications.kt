package net.cakebuild.shared

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object CakeBalloonNotifications {
    // this will raise a deprecation notice. Sadly that's unavoidable until
    // we drop support for all versions < 2020.3.
    // after 2020.3 this should be done in pluxin.xml, see https://jetbrains.org/intellij/sdk/docs/user_interface_components/notifications.html
    private val notificationGroup =
        NotificationGroup("Cake", NotificationDisplayType.BALLOON, true)

    private fun notify(project: Project, content: String, type: NotificationType) {
        notificationGroup.createNotification(content, type).notify(project)
    }

    fun notifyError(project: Project, content: String) {
        notify(project, content, NotificationType.ERROR)
    }
    fun notifyInformation(project: Project, content: String) {
        notify(project, content, NotificationType.INFORMATION)
    }
    fun notifyWarning(project: Project, content: String) {
        notify(project, content, NotificationType.WARNING)
    }
}
