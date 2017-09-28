package net.cakebuild.run

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.model.RunnableProjectKind
import com.jetbrains.rider.run.configurations.DotNetRunConfigurationBase
import com.jetbrains.rider.runtime.RiderDotNetActiveRuntimeHost
import net.cakebuild.util.CakeFilePath

class CakeConfiguration(project: Project, factory: CakeConfigurationFactory, activeRuntimeHost: RiderDotNetActiveRuntimeHost)
    : DotNetRunConfigurationBase("Cake", project, factory, activeRuntimeHost) {

    var task: String? = null

    private val workingDirectory: VirtualFile?
        get() = CakeFilePath.getCakeFilePath(project)?.parent

    override val executeAsIs = false
    override val runtimeArguments: String? = null
    override val targetEnvironmentVariables: Map<String, String> = mapOf()
    override val targetProjectKind = RunnableProjectKind.Console
    override val targetUseExternalConsole = false
    override val targetUseMonoRuntime = false
    override val targetWorkingDirectory: String
        get() = workingDirectory?.path ?: ""

    override val targetExecutablePath: String
        get() = workingDirectory?.findFileByRelativePath("tools/Cake/Cake.exe")?.path ?: ""

    override val targetArguments: List<String>
        // TODO: Add --debug for debugging
        get() = listOf("--target=\"$task\"")

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return CakeConfigurationEditor()
    }
}