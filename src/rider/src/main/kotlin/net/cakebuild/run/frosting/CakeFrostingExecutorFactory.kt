package net.cakebuild.run.frosting

import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rider.run.configurations.AsyncExecutorFactory
import com.jetbrains.rider.run.configurations.RunnableProjectKinds
import com.jetbrains.rider.run.configurations.project.DotNetProjectConfigurationParameters
import com.jetbrains.rider.run.configurations.project.DotNetProjectExecutorFactory
import com.jetbrains.rider.run.configurations.project.DotNetStartBrowserParameters

class CakeFrostingExecutorFactory(val project: Project, val parameters: CakeFrostingConfigurationParameters) :
    AsyncExecutorFactory {
    override suspend fun create(executorId: String, environment: ExecutionEnvironment, lifetime: Lifetime):
        RunProfileState {
        return DotNetProjectExecutorFactory(
            project,
            DotNetProjectConfigurationParameters(
                project,
                exePath = "",
                programParameters = parameters.programArguments,
                workingDirectory = "",
                envs = parameters.envs,
                isPassParentEnvs = true,
                useExternalConsole = false,
                runtimeArguments = "",
                projectFilePath = parameters.projectFilePath,
                trackProjectExePath = true,
                trackProjectArguments = true,
                trackProjectWorkingDirectory = true,
                projectKind = RunnableProjectKinds.DotNetCore,
                projectTfm = "",
                startBrowserParameters = DotNetStartBrowserParameters(),
                runtimeType = null
            )
        ).create(executorId, environment, lifetime)
    }
}
