package net.cakebuild.beforeRunner

import com.intellij.execution.BeforeRunTask
import com.intellij.execution.BeforeRunTaskProvider
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.util.Key
import icons.CakeIcons
import net.cakebuild.installers.CakeNetToolInstaller
import net.cakebuild.run.script.CakeScriptConfiguration
import net.cakebuild.shared.CakeBalloonNotifications

@Suppress("DialogTitleCapitalization")
class EnsureNetToolBeforeRunTaskProvider :
    BeforeRunTaskProvider<EnsureNetToolBeforeRunTaskProvider.EnsureNetToolBeforeRunTask>() {
    companion object {
        val providerId = Key.create<EnsureNetToolBeforeRunTask>("Cake.EnsureNetToolBeforeRunTask")
    }

    override fun getId() = providerId

    override fun getName() = "Ensure .NET Tool (Global)"

    override fun getIcon() = CakeIcons.CakeAction

    override fun getDescription(task: EnsureNetToolBeforeRunTask) = "Ensure Cake .NET Tool (Global) is installed"

    override fun createTask(runConfiguration: RunConfiguration): EnsureNetToolBeforeRunTask? {
        if (runConfiguration is CakeScriptConfiguration) {
            return EnsureNetToolBeforeRunTask()
        }

        return null
    }

    override fun executeTask(
        context: DataContext,
        configuration: RunConfiguration,
        env: ExecutionEnvironment,
        task: EnsureNetToolBeforeRunTask
    ): Boolean {
        val installer = CakeNetToolInstaller()
        val installed = installer.getInstalledCakeVersion()
        if (installed != null) {
            return true
        }

        val ok = installer.installCake()
        if (ok) {
            CakeBalloonNotifications.notifyInformation(
                env.project,
                "Cake.Tool (Global) successfully installed."
            )
        } else {
            CakeBalloonNotifications.notifyError(
                env.project,
                "Installation of Cake.Tool (Global) failed."
            )
        }
        return ok
    }

    class EnsureNetToolBeforeRunTask :
        BeforeRunTask<EnsureNetToolBeforeRunTask>(providerId)
}
