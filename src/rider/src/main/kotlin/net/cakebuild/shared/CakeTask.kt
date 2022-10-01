package net.cakebuild.shared

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rd.util.reactive.valueOrThrow
import net.cakebuild.protocol.CakeFrostingProject
import net.cakebuild.run.frosting.CakeFrostingConfiguration
import net.cakebuild.run.frosting.CakeFrostingConfigurationType

enum class CakeTaskRunMode {
    Run,
    Debug,
    SaveConfigOnly
}

abstract class CakeTask(val project: Project, val name: String) {
    abstract fun run(mode: CakeTaskRunMode)
}

class CakeScriptTask(project: Project, val file: VirtualFile, taskName: String) : CakeTask(project, taskName) {
    override fun run(mode: CakeTaskRunMode) {
        CakeScriptProject.runCakeTarget(project, file, name, mode)
    }
}

class CakeFrostingTask(project: Project, val cakeProject: CakeFrostingProject, taskName: String) :
    CakeTask(project, taskName) {
    override fun run(mode: CakeTaskRunMode) {
        val runManager = project.getService(RunManager::class.java)
        val configurationType = ConfigurationTypeUtil.findConfigurationType(CakeFrostingConfigurationType::class.java)
        val cfgName = runManager.suggestUniqueName("${cakeProject.name.valueOrNull}: $name", configurationType)
        val runConfiguration = runManager.createConfiguration(cfgName, configurationType.factory)
        val cakeConfiguration = runConfiguration.configuration as CakeFrostingConfiguration

        cakeConfiguration.parameters.projectFilePath = cakeProject.projectFilePath.valueOrThrow
        cakeConfiguration.parameters.taskName = name

        val executor = when (mode) {
            CakeTaskRunMode.Debug -> DefaultDebugExecutor.getDebugExecutorInstance()
            CakeTaskRunMode.Run -> DefaultRunExecutor.getRunExecutorInstance()
            else -> {
                runConfiguration.storeInDotIdeaFolder()
                runManager.addConfiguration(runConfiguration)
                runManager.selectedConfiguration = runConfiguration
                null
            }
        }

        if (executor != null) {
            ProgramRunnerUtil.executeConfiguration(runConfiguration, executor)
        }
    }
}
