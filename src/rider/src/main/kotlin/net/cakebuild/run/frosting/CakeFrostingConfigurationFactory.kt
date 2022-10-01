package net.cakebuild.run.frosting

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy
import com.intellij.openapi.project.Project

class CakeFrostingConfigurationFactory(cakeConfigurationType: CakeFrostingConfigurationType) :
    ConfigurationFactory(cakeConfigurationType) {

    override fun getSingletonPolicy() = RunConfigurationSingletonPolicy.SINGLE_INSTANCE

    override fun createTemplateConfiguration(project: Project): CakeFrostingConfiguration {
        val defaultParameters = CakeFrostingConfigurationParameters(project)
        return CakeFrostingConfiguration(project, this, defaultParameters)
    }

    override fun getName(): String {
        return id
    }

    override fun getId(): String {
        return "CakeFrostingConfigurationFactory"
    }
}
