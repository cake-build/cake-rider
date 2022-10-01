package net.cakebuild.run.frosting

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.jetbrains.rider.debugger.IRiderDebuggable
import com.jetbrains.rider.run.ICanRunFromBackend
import com.jetbrains.rider.run.configurations.IDotNetRunConfigurationWithPostStartupActivitiesSupport
import com.jetbrains.rider.run.configurations.IProjectBasedRunConfiguration
import com.jetbrains.rider.run.configurations.RiderAsyncRunConfiguration
import org.jdom.Element

class CakeFrostingConfiguration(
    project: Project,
    private val factory: CakeFrostingConfigurationFactory,
    val parameters: CakeFrostingConfigurationParameters
) :
    RiderAsyncRunConfiguration(
        "Cake Frosting",
        project,
        factory,
        ::CakeFrostingConfigurationEditor,
        CakeFrostingExecutorFactory(project, parameters)
    ),
    IRiderDebuggable,
    ICanRunFromBackend,
    IProjectBasedRunConfiguration,
    IDotNetRunConfigurationWithPostStartupActivitiesSupport {

    override fun getProjectFilePath(): String {
        return parameters.projectFilePath
    }

    override fun setProjectFilePath(path: String) {
        parameters.projectFilePath = path
    }

    override fun getTypeId(): String {
        return type.id
    }

    override fun writeExternal(element: Element) {
        parameters.writeExternal(element)
    }

    override fun readExternal(element: Element) {
        parameters.readExternal(element)
    }

    override fun clone(): RunConfiguration {
        val clone = CakeFrostingConfiguration(project, factory, parameters.copy())
        clone.doCopyOptionsFrom(this)
        copyCopyableDataTo((clone as UserDataHolderBase))
        return clone
    }

    override fun canGenerateFromBackend(): Boolean {
        return true
    }

    override fun checkConfiguration() {
        parameters.validate()
    }
}
