package net.cakebuild.run.frosting

import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.TextFieldWithAutoCompletionListProvider
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.SequentialLifetimes
import com.jetbrains.rd.util.reactive.IProperty
import com.jetbrains.rd.util.reactive.ISignal
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.Signal
import com.jetbrains.rd.util.reactive.valueOrThrow
import com.jetbrains.rider.model.RunnableProject
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.run.RiderRunBundle
import com.jetbrains.rider.run.configurations.RunnableProjectKinds
import com.jetbrains.rider.run.configurations.controls.ControlBase
import com.jetbrains.rider.run.configurations.controls.EnvironmentVariablesEditor
import com.jetbrains.rider.run.configurations.controls.ProgramParametersEditor
import com.jetbrains.rider.run.configurations.controls.ProjectSelector
import com.jetbrains.rider.run.configurations.controls.RunConfigurationViewModelBase
import com.jetbrains.rider.run.configurations.controls.bindTo
import com.jetbrains.rider.run.configurations.runnableProjectsModelIfAvailable
import net.cakebuild.protocol.CakeFrostingProject
import net.cakebuild.protocol.CakeFrostingProjectsModel
import net.cakebuild.protocol.cakeFrostingProjectsModel
import net.cakebuild.run.CustomControlBase
import net.cakebuild.run.VerbositySelector
import java.io.File
import javax.swing.JComponent

class TaskEditor(lifetime: Lifetime, name: String, id: String) : CustomControlBase(lifetime, name, id) {
    private val defaultValue: String = "Default"
    val text: IProperty<String> = Property(defaultValue)

    val refreshCompletionList: ISignal<CakeFrostingProject?> = Signal()

    override val valueCanBeReverted: IProperty<Boolean> = Property(false)

    override fun revert() {
        text.set(defaultValue)
    }

    override fun build(project: Project): JComponent {
        val provider =
            object : TextFieldWithAutoCompletionListProvider<String>(emptyList()) {
                override fun getLookupString(item: String): String {
                    return item
                }

                private val sequentialLifetime: SequentialLifetimes = SequentialLifetimes(lifetime)

                fun updateCompletionItems(project: CakeFrostingProject?) {
                    if (project == null) {
                        this.sequentialLifetime.next()
                    } else {
                        this.setItems(project.tasks)
                    }
                }
            }

        refreshCompletionList.advise(lifetime, provider::updateCompletionItems)

        val textField = TextFieldWithAutoCompletion(project, provider, false, null)
        text.advise(lifetime, textField::setText)
        textField.document.addDocumentListener(
            object : DocumentListener {
                override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
                    text.set(textField.text)
                }
            },
        )

        return makeLabeledComponent(textField)
    }
}

class CakeFrostingConfigurationViewModel(private val lifetime: Lifetime, private val project: Project) :
    RunConfigurationViewModelBase() {
    private var loaded = false

    private val projectSelector = ProjectSelector(RiderRunBundle.message("label.project.with.colon"), "Project")

    // private val tfmSelector: StringSelector =
    //     StringSelector(RiderRunBundle.message("label.target.framework.with.colon"), "Target_framework")
    private val taskEditor: TaskEditor = TaskEditor(lifetime, "Task:", "Task")
    private val verbositySelector: VerbositySelector = VerbositySelector(lifetime, "Verbosity:", "Verbosity")
    private val programParametersEditor: ProgramParametersEditor =
        ProgramParametersEditor("Arguments:", "Program_arguments", lifetime)
    private val environmentVariablesEditor: EnvironmentVariablesEditor =
        EnvironmentVariablesEditor(
            RiderRunBundle.message("label.environment.variables.with.colon"),
            "Environment_variables",
        )

    override val controls: List<ControlBase> =
        mutableListOf(
            projectSelector,
            taskEditor,
            verbositySelector,
            programParametersEditor,
            environmentVariablesEditor,
        )

    private fun onProjectChange(project: RunnableProject) {
        val cakeFrostingProjectsModel = this.project.solution.cakeFrostingProjectsModel
        taskEditor.refreshCompletionList.fire(cakeFrostingProjectsModel.getProjectFor(project))
    }

    fun reset(parameters: CakeFrostingConfigurationParameters) {
        if (!loaded) {
            val runnableProjectsModel = project.runnableProjectsModelIfAvailable!!
            val cakeFrostingProjectsModel = project.solution.cakeFrostingProjectsModel

            projectSelector.bindTo(
                runnableProjectsModel,
                lifetime,
                { it.kind == RunnableProjectKinds.DotNetCore && cakeFrostingProjectsModel.getProjectFor(it) != null },
                { },
                this::onProjectChange,
            )

            loaded = true
        }

        val projectFilePath = parameters.projectFilePath
        if (projectFilePath.isNotEmpty()) {
            var project = projectSelector.projectList.singleOrNull { it.projectFilePath == projectFilePath }

            if (project == null) {
                val name = File(projectFilePath).name
                project =
                    RunnableProject(
                        name,
                        name,
                        projectFilePath,
                        RunnableProjectKinds.DotNetCore,
                        listOf(),
                        listOf(),
                        null,
                        listOf(),
                    )
                projectSelector.projectList.add(project)
            }

            projectSelector.project.set(project)
            onProjectChange(project)
        }

        taskEditor.text.set(parameters.taskName)
        verbositySelector.verbosity.set(parameters.verbosity)
        programParametersEditor.parametersString.set(parameters.additionalArguments)
        environmentVariablesEditor.envs.set(parameters.envs)
    }

    fun apply(parameters: CakeFrostingConfigurationParameters) {
        val project = projectSelector.project.valueOrNull
        if (project != null) {
            parameters.projectFilePath = project.projectFilePath
        }

        parameters.taskName = taskEditor.text.value
        parameters.verbosity = verbositySelector.verbosity.value
        parameters.additionalArguments = programParametersEditor.parametersString.value
        parameters.envs = environmentVariablesEditor.envs.value
    }
}

fun CakeFrostingProjectsModel.getProjectFor(project: RunnableProject): CakeFrostingProject? {
    return this.projects.singleOrNull { it.projectFilePath.valueOrThrow == project.projectFilePath }
}
