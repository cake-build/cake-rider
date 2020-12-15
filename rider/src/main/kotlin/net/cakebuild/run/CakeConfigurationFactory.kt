package net.cakebuild.run

import com.intellij.execution.BeforeRunTask
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.jetbrains.rider.build.tasks.BuildProjectBeforeRunTask
import com.jetbrains.rider.build.tasks.BuildProjectBeforeRunTaskProvider

class CakeConfigurationFactory(cakeConfigurationType: CakeConfigurationType) :
    ConfigurationFactory(cakeConfigurationType) {

    override fun isConfigurationSingletonByDefault() = true

    override fun createTemplateConfiguration(project: Project): CakeConfiguration {
        return CakeConfiguration(project, this)
    }

    override fun getName(): String {
        return "Cake configuration factory"
    }

    override fun getOptionsClass(): Class<out BaseState> {
        return CakeConfigurationOptions::class.java
    }

    // Do not build the project before running cake-tasks
    override fun configureBeforeRunTaskDefaults(
        providerID: Key<out BeforeRunTask<BeforeRunTask<*>>>?,
        task: BeforeRunTask<out BeforeRunTask<*>>?
    ) {
        if (providerID == BuildProjectBeforeRunTaskProvider.providerId &&
            task is BuildProjectBeforeRunTask
        ) {
            task.isEnabled = false
        }
    }
}
