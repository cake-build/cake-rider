package net.cakebuild.run

import com.intellij.execution.BeforeRunTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.jetbrains.rider.build.tasks.BuildProjectBeforeRunTask
import com.jetbrains.rider.build.tasks.BuildProjectBeforeRunTaskProvider
import com.jetbrains.rider.run.configurations.DotNetConfigurationFactoryBase
import com.jetbrains.rider.runtime.RiderDotNetActiveRuntimeHost
import com.jetbrains.rider.util.idea.getComponent

class CakeConfigurationFactory(cakeConfigurationType: CakeConfigurationType)
    : DotNetConfigurationFactoryBase<CakeConfiguration>(cakeConfigurationType) {

    override fun isConfigurationSingletonByDefault() = true

    override fun createTemplateConfiguration(project: Project): CakeConfiguration {
        val host = project.getComponent<RiderDotNetActiveRuntimeHost>()
        return CakeConfiguration(project, this, host)
    }

    override fun configureBeforeRunTaskDefaults(providerID: Key<out BeforeRunTask<BeforeRunTask<*>>>?, task: BeforeRunTask<out BeforeRunTask<*>>?) {
        if (providerID == BuildProjectBeforeRunTaskProvider.buildId && task is BuildProjectBeforeRunTask) {
            task.isEnabled = false
        }
    }
}