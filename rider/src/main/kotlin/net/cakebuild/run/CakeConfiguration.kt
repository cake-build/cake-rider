package net.cakebuild.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class CakeConfiguration(project: Project, factory: CakeConfigurationFactory)
    : RunConfigurationBase<CakeConfigurationOptions>(project, factory, "Cake") {

    var task: String
        get() {
            return options.task
        }
        set(value) {
            options.task = value
        }

    // TODO: Verbosity & scriptPath

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                val commandLine = GeneralCommandLine(options.getCommandline())
                val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return CakeConfigurationEditor()
    }

    override fun getOptions(): CakeConfigurationOptions {
        return super.getOptions() as CakeConfigurationOptions
    }
}

class CakeConfigurationOptions : RunConfigurationOptions() {
    var task = "Default"
    private var scriptPath = "build.cake"
    private var verbosity = "normal"

    fun getCommandline(): String {
        return "dotnet cake $scriptPath --target=\"$task\" --verbosity=$verbosity"
    }
}

// original (non-compiling) code:
// reason for having this: I'm really not sure if my (compiling) replacement does the same...
/*
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
 */

