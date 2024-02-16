package net.cakebuild.run.script

import com.intellij.execution.BeforeRunTask
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.jetbrains.rider.build.tasks.BuildProjectBeforeRunTask
import com.jetbrains.rider.build.tasks.BuildProjectBeforeRunTaskProvider

class CakeScriptConfigurationFactory(cakeConfigurationType: CakeScriptConfigurationType) :
    ConfigurationFactory(cakeConfigurationType) {
    override fun getSingletonPolicy() = RunConfigurationSingletonPolicy.SINGLE_INSTANCE

    override fun createTemplateConfiguration(project: Project): CakeScriptConfiguration {
        return CakeScriptConfiguration(project, this)
    }

    override fun getName(): String {
        return id
    }

    override fun getId(): String {
        return "Cake configuration factory"
    }

    override fun getOptionsClass(): Class<out BaseState> {
        return CakeScriptConfigurationOptions::class.java
    }

    override fun configureBeforeRunTaskDefaults(
        providerID: Key<out BeforeRunTask<BeforeRunTask<*>>>?,
        task: BeforeRunTask<out BeforeRunTask<*>>?,
    ) {
        super.configureBeforeRunTaskDefaults(providerID, task)

        // Do not build the project before running cake-tasks
        if (providerID == BuildProjectBeforeRunTaskProvider.providerId &&
            task is BuildProjectBeforeRunTask
        ) {
            task.isEnabled = false
        }
    }
}
