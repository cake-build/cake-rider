package net.cakebuild.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.nio.file.FileSystems


class CakeConfiguration(project: Project, factory: CakeConfigurationFactory)
    : RunConfigurationBase<CakeConfigurationOptions>(project, factory, "Cake") {

    fun setOptions(filePath: String, taskName: String, verbosity: String = "normal"){
        val options = options
        options.scriptPath = filePath
        options.taskName = taskName
        options.verbosity = verbosity
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment)
        : RunProfileState {
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                var exe = "dotnet-cake"
                if(System.getProperty("os.name") == "win32"){
                    exe = "dotnet-cake.exe"
                }
                val options = options
                val fileSystems = FileSystems.getDefault()
                val scriptPath = fileSystems.getPath(project.basePath).resolve(fileSystems.getPath(options.scriptPath))
                val commandLine = GeneralCommandLine()
                    .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                    .withWorkDirectory(scriptPath.parent.toString())
                    .withExePath(exe)
                    .withParameters(
                        scriptPath.toString(),
                        "--target=${options.taskName}",
                        "--verbosity=${options.verbosity}")
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

