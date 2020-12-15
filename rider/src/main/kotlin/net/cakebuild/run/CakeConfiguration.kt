package net.cakebuild.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import net.cakebuild.settings.CakeSettings
import java.nio.file.FileSystems

class CakeConfiguration(project: Project, factory: CakeConfigurationFactory) :
    RunConfigurationBase<CakeConfigurationOptions>(project, factory, "Cake") {

    private val log = Logger.getInstance(CakeConfiguration::class.java)

    fun setOptions(filePath: String, taskName: String, verbosity: String) {
        val options = options
        options.scriptPath = filePath
        options.taskName = taskName
        options.verbosity = verbosity
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment):
        RunProfileState {
            return object : CommandLineState(environment) {
                override fun startProcess(): ProcessHandler {
                    val settings = CakeSettings.getInstance(project)
                    val os = System.getProperty("os.name")
                    var exe = settings.cakeRunner
                    settings.cakeRunnerOverrides.forEach forEach@{
                        val regex = Regex(it.key, RegexOption.IGNORE_CASE)
                        if (regex.matches(os)) {
                            log.trace("os $os matches regex ${it.key}")
                            exe = it.value
                            return@forEach
                        }
                    }
                    log.info("cake runner is set to $exe")
                    val options = options
                    val fileSystems = FileSystems.getDefault()
                    val scriptPath = fileSystems
                        .getPath(project.basePath!!)
                        .resolve(fileSystems.getPath(options.scriptPath!!))
                    val commandLine = GeneralCommandLine()
                        .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                        .withWorkDirectory(scriptPath.parent.toString())
                        .withExePath(exe)
                        .withParameters(
                            scriptPath.toString(),
                            "--target=${options.taskName}",
                            "--verbosity=${options.verbosity}"
                        )
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
