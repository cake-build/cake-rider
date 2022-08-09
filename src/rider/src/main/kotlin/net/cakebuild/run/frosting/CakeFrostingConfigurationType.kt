package net.cakebuild.run.frosting

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.jetbrains.rd.ide.model.RunConfiguration
import com.jetbrains.rd.ide.model.RunConfigurationTemplate
import com.jetbrains.rd.ide.model.RunConfigurationTemplateKey
import com.jetbrains.rider.model.RunnableProjectKind
import com.jetbrains.rider.run.ICanCreateFromBackend
import com.jetbrains.rider.run.configurations.IRunnableProjectConfigurationType
import com.jetbrains.rider.run.configurations.RunnableProjectKinds
import icons.CakeIcons

class CakeFrostingConfigurationType :
    ConfigurationTypeBase(id, "Cake Frosting", "Cake Frosting", CakeIcons.CakeAction), IRunnableProjectConfigurationType, ICanCreateFromBackend {

    val factory = CakeFrostingConfigurationFactory(this)

    init {
        addFactory(factory)
    }

    override fun isApplicable(kind: RunnableProjectKind): Boolean {
        return kind == RunnableProjectKinds.DotNetCore
    }

    override fun createFromTemplate(runConfigurationTemplate: RunConfigurationTemplate, runManager: RunManager): RunConfiguration {
        val name = runConfigurationTemplate.entries.single { it.key == RunConfigurationTemplateKey.Name }.value
        val projectFilePath = runConfigurationTemplate.entries.single { it.key == RunConfigurationTemplateKey.ProjectFilePath }.value
        val taskName = runConfigurationTemplate.entries.single { it.key == RunConfigurationTemplateKey.StaticMethodName }.value

        val configurationSettings = runManager.createConfiguration(name, factory)
        configurationSettings.isTemporary = true

        val configuration = configurationSettings.configuration as CakeFrostingConfiguration
        configuration.parameters.apply {
            this.projectFilePath = projectFilePath
            this.taskName = taskName
        }

        runManager.addConfiguration(configurationSettings)

        return RunConfiguration(runConfigurationTemplate.typeId, name, projectFilePath, runConfigurationTemplate.executor)
    }

    override fun getCompatibleRunnableProjectKinds(): List<String> {
        return listOf(id, RunnableProjectKinds.DotNetCore.name)
    }


    companion object {
        const val id = "CakeFrosting"
    }
}
