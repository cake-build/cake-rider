package net.cakebuild.beforeRunner

import com.intellij.execution.BeforeRunTask
import com.intellij.execution.BeforeRunTaskProvider
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import icons.CakeIcons
import net.cakebuild.run.script.CakeScriptConfiguration
import net.cakebuild.shared.CakeBalloonNotifications

class RestoreNetToolsBeforeRunTaskProvider :
    BeforeRunTaskProvider<RestoreNetToolsBeforeRunTaskProvider.RestoreNetToolsBeforeRunTask>() {
    companion object {
        val providerId = Key.create<RestoreNetToolsBeforeRunTask>("Cake.RestoreNetToolsBeforeRunTask")
    }

    private val log = Logger.getInstance(RestoreNetToolsBeforeRunTaskProvider::class.java)

    override fun getId() = providerId

    override fun getName() = "Restore .NET Tools"

    override fun getIcon() = CakeIcons.CakeAction

    override fun createTask(runConfiguration: RunConfiguration): RestoreNetToolsBeforeRunTask? {
        if (runConfiguration is CakeScriptConfiguration) {
            return RestoreNetToolsBeforeRunTask()
        }

        return null
    }

    override fun executeTask(
        context: DataContext,
        configuration: RunConfiguration,
        env: ExecutionEnvironment,
        task: RestoreNetToolsBeforeRunTask,
    ): Boolean {
        val commandLine =
            GeneralCommandLine()
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                .withExePath("dotnet")
                .withParameters("tool", "restore")
                .withWorkDirectory((configuration as CakeScriptConfiguration).getWorkingDirectory())
        val output = ExecUtil.execAndGetOutput(commandLine)
        if (output.exitCode != 0) {
            val err = "'${commandLine.commandLineString}' exited with unexpected Exitcode. Exitcode: ${output.exitCode}"
            log.warn(err)
            log.warn(output.stdout)
            log.warn(output.stderr)
            CakeBalloonNotifications.notifyError(env.project, err)
        }

        return output.exitCode == 0
    }

    class RestoreNetToolsBeforeRunTask :
        BeforeRunTask<RestoreNetToolsBeforeRunTask>(providerId)
}
