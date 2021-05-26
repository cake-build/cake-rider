package net.cakebuild.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import net.cakebuild.installers.CakeNetToolInstaller
import net.cakebuild.shared.CakeBalloonNotifications

abstract class CakeToolInstallAction : AnAction(), DumbAware {
    class DotNetToolInstallAction : CakeToolInstallAction() {
        companion object {
            const val PROCESS_START: Double = 0.1
            const val PROCESS_ONE_THIRD: Double = 0.33
            const val PROCESS_HALF: Double = 0.5
            const val PROCESS_TWO_THIRDS: Double = 0.67
            const val PROCESS_DONE: Double = 1.0
        }
        override fun actionPerformed(e: AnActionEvent) {
            val task: Task.Backgroundable = object : Task.Backgroundable(e.project, "Ensuring Cake.Tool (Global)") {
                override fun run(indicator: ProgressIndicator) {
                    doInstallOrUpdate(e.project, indicator)
                }
            }
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(
                task,
                BackgroundableProcessIndicator(task)
            )
        }

        private fun doInstallOrUpdate(project: Project?, indicator: ProgressIndicator) {
            if (indicator.isCanceled) {
                return
            }
            indicator.isIndeterminate = false
            indicator.fraction = PROCESS_START
            indicator.text = "Checking Cake.Tool (Global) installed version"
            val installer = CakeNetToolInstaller()
            val installedVersion = installer.getInstalledCakeVersion()
            if (indicator.isCanceled) {
                return
            }
            if (installedVersion == null) {
                indicator.fraction = PROCESS_HALF
                indicator.text = "Installing Cake.Tool (Global)"
                installCakeTool(installer, project)
                indicator.fraction = PROCESS_DONE
                return
            }

            indicator.fraction = PROCESS_ONE_THIRD
            indicator.text = "Checking available Cake.Tool version"
            val latestVersion = installer.getLatestAvailableVersion()
            if (indicator.isCanceled) {
                return
            }
            if (latestVersion == null || latestVersion <= installedVersion) {
                indicator.fraction = PROCESS_DONE
                if (project != null) {
                    ApplicationManager.getApplication().invokeLater {
                        CakeBalloonNotifications.notifyInformation(
                            project,
                            "Cake.Tool (Global) already installed."
                        )
                    }
                }
                return
            }

            indicator.fraction = PROCESS_TWO_THIRDS
            indicator.text = "Updating Cake.Tool (Global) version"
            updateCakeTool(installer, project)
            indicator.fraction = PROCESS_DONE
        }

        private fun updateCakeTool(installer: CakeNetToolInstaller, project: Project?) {
            val success = installer.updateCake()
            if (project == null) {
                return
            }

            ApplicationManager.getApplication().invokeLater {
                if (success) {
                    CakeBalloonNotifications.notifyInformation(
                        project,
                        "Cake.Tool (Global) successfully updated."
                    )
                } else {
                    CakeBalloonNotifications.notifyError(
                        project,
                        "update of Cake.Tool (Global) failed."
                    )
                }
            }
        }

        private fun installCakeTool(installer: CakeNetToolInstaller, project: Project?) {
            val success = installer.installCake()
            if (project == null) {
                return
            }

            ApplicationManager.getApplication().invokeLater {
                if (success) {
                    CakeBalloonNotifications.notifyInformation(
                        project,
                        "Cake.Tool (Global) successfully installed."
                    )
                } else {
                    CakeBalloonNotifications.notifyError(
                        project,
                        "installation of Cake.Tool (Global) failed."
                    )
                }
            }
        }
    }
}
