package net.cakebuild.run

import com.intellij.execution.RunManager
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.runConfigurationModel
import com.jetbrains.rd.platform.util.idea.ProtocolSubscribedProjectComponent
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rider.projectView.solution
import net.cakebuild.protocol.cakeFrostingProjectsModel
import net.cakebuild.run.frosting.CakeFrostingConfiguration
import net.cakebuild.run.frosting.CakeFrostingConfigurationType

class CakeConfigurationHost(project: Project) : ProtocolSubscribedProjectComponent(project) {
    private val model = project.solution.cakeFrostingProjectsModel
    private val runManager = RunManager.getInstance(project)

    init {
        project.solution.runConfigurationModel.runConfigurations.advise(project.lifetime) { runConfigurations ->
            model.runConfigurationTaskNames.clear()

            for (runConfiguration in runConfigurations) {
                if (runConfiguration.typeId != CakeFrostingConfigurationType.id) continue
                val configurationSettings =
                    runManager.findConfigurationByTypeAndName(runConfiguration.typeId, runConfiguration.name)
                        ?: continue
                val configuration = configurationSettings.configuration as CakeFrostingConfiguration

                model.runConfigurationTaskNames[runConfiguration] = configuration.parameters.taskName
            }
        }
    }
}
