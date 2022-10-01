package net.cakebuild.run

import com.intellij.openapi.project.Project
import com.jetbrains.rider.run.configurations.RiderNewRunConfigurationTreeGroupingProvider
import icons.CakeIcons
import net.cakebuild.run.frosting.CakeFrostingConfigurationType
import net.cakebuild.run.script.CakeScriptConfigurationType

class CakeRiderNewRunConfigurationTreeGroupingProvider : RiderNewRunConfigurationTreeGroupingProvider {
    override fun getGroups(project: Project): List<RiderNewRunConfigurationTreeGroupingProvider.Group> {
        return listOf(
            RiderNewRunConfigurationTreeGroupingProvider.Group(
                CakeIcons.CakeAction, "Cake",
                listOf(
                    CakeFrostingConfigurationType.id,
                    CakeScriptConfigurationType.id
                )
            )
        )
    }
}
