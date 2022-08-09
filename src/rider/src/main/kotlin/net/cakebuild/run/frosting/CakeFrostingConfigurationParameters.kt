package net.cakebuild.run.frosting

import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.run.RiderRunBundle
import com.jetbrains.rider.run.configurations.RunConfigurationHelper.copyEnvs
import net.cakebuild.protocol.cakeFrostingProjectsModel
import net.cakebuild.shared.ui.VerbosityComboBox
import org.jdom.Element

class CakeFrostingConfigurationParameters(val project: Project) {
    var projectFilePath: String = ""
    var taskName: String = "Default"
    var verbosity: String = VerbosityComboBox.DEFAULT
    var additionalArguments: String = ""
    var envs: Map<String, String> = mutableMapOf()

    val programArguments: String
        get() = "--target=\"${taskName.ifEmpty { "Default" }}\" --verbosity=\"$verbosity\" $additionalArguments"

    constructor(project: Project, taskName: String, projectFilePath: String, verbosity: String, programParameters: String, envs: Map<String, String>) : this(project) {
        this.projectFilePath = projectFilePath
        this.taskName = taskName
        this.verbosity = verbosity
        this.additionalArguments = programParameters
        this.envs = envs
    }

    fun writeExternal(element: Element) {
        JDOMExternalizerUtil.writeField(element, "PROJECT_PATH", projectFilePath)
        JDOMExternalizerUtil.writeField(element, "TASK_NAME", this.taskName)
        JDOMExternalizerUtil.writeField(element, "VERBOSITY", verbosity)
        JDOMExternalizerUtil.writeField(element, "PROGRAM_PARAMETERS", additionalArguments)
        EnvironmentVariablesComponent.writeExternal(element, envs)
    }

    fun readExternal(element: Element) {
        projectFilePath = JDOMExternalizerUtil.readField(element, "PROJECT_PATH") ?: ""
        taskName = JDOMExternalizerUtil.readField(element, "TASK_NAME") ?: "Default"
        verbosity = JDOMExternalizerUtil.readField(element, "VERBOSITY") ?: VerbosityComboBox.DEFAULT
        additionalArguments = JDOMExternalizerUtil.readField(element, "PROGRAM_PARAMETERS") ?: ""
        EnvironmentVariablesComponent.readExternal(element, envs)
    }

    fun copy(): CakeFrostingConfigurationParameters {
        return CakeFrostingConfigurationParameters(project, taskName, projectFilePath, verbosity, additionalArguments, copyEnvs(envs))
    }

    fun validate() {
        if (projectFilePath.isEmpty() || this.project.solution.cakeFrostingProjectsModel.projects.all { it.projectFilePath.valueOrNull != projectFilePath }) {
            throw RuntimeConfigurationError(RiderRunBundle.message("DotNetProjectConfigurationParameters.not.specified.project"))
        }
    }
}