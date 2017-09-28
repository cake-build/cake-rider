package net.cakebuild.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.jetbrains.rider.run.configurations.DotNetConfigurationFactoryBase
import icons.CakeIcons

class CakeConfigurationType : ConfigurationTypeBase("CAKE_CONFIGURATION", "Cake", "Cake", CakeIcons.Cake) {

    val cakeFactory = CakeConfigurationFactory(this)

    init {
        addFactory(cakeFactory)
    }
}

class CakeConfigurationFactory(cakeConfigurationType: CakeConfigurationType)
    : DotNetConfigurationFactoryBase<CakeConfiguration>(cakeConfigurationType) {

    override fun createTemplateConfiguration(project: Project) = CakeConfiguration(project, this)
}

class CakeConfiguration(project: Project, factory: CakeConfigurationFactory)
    : RunConfigurationBase(project, factory, "Cake") {

    override fun checkConfiguration() {
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getState(p0: Executor, p1: ExecutionEnvironment): RunProfileState? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
