package net.cakebuild.run

import com.intellij.openapi.project.Project
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
}