package net.cakebuild.run.script

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rider.run.configurations.LifetimedSettingsEditor
import com.jetbrains.rider.run.configurations.controls.ControlBase
import com.jetbrains.rider.run.configurations.controls.ControlViewBuilder
import com.jetbrains.rider.run.configurations.controls.PathSelector
import com.jetbrains.rider.run.configurations.controls.ProgramParametersEditor
import com.jetbrains.rider.run.configurations.controls.RunConfigurationViewModelBase
import com.jetbrains.rider.run.configurations.controls.TextEditor
import net.cakebuild.language.CakeFileType
import net.cakebuild.run.VerbositySelector
import net.cakebuild.run.customBuild
import javax.swing.JComponent

class CakeScriptConfigurationViewModel(lifetime: Lifetime, project: Project) : RunConfigurationViewModelBase() {
    private val scriptPathSelector: PathSelector = PathSelector("Script path:", "Script_path", FileChooserDescriptorFactory.createSingleFileDescriptor(CakeFileType.INSTANCE), lifetime)
    private val taskEditor: TextEditor = TextEditor("Task:", "Task", lifetime)
    private val verbositySelector: VerbositySelector = VerbositySelector(lifetime, "Verbosity:", "Verbosity")
    private val programParametersEditor: ProgramParametersEditor = ProgramParametersEditor("Arguments:", "Program_arguments", lifetime)

    init {
        scriptPathSelector.rootDirectory.set(project.basePath)
    }

    override val controls: List<ControlBase> = mutableListOf(scriptPathSelector, taskEditor, verbositySelector, programParametersEditor)

    fun reset(parameters: CakeScriptConfigurationOptions) {
        scriptPathSelector.path.set(parameters.scriptPath.orEmpty())
        taskEditor.text.set(parameters.taskName.orEmpty())
        verbositySelector.verbosity.set(parameters.verbosity.orEmpty())
        programParametersEditor.parametersString.set(parameters.additionalArguments.orEmpty())
    }

    fun apply(parameters: CakeScriptConfigurationOptions) {
        parameters.scriptPath = FileUtil.toSystemIndependentName(scriptPathSelector.path.value)
        parameters.taskName = taskEditor.text.value
        parameters.verbosity = verbositySelector.verbosity.value
        parameters.additionalArguments = programParametersEditor.parametersString.value
    }
}

class CakeScriptConfigurationEditor(private val project: Project) : LifetimedSettingsEditor<CakeScriptConfiguration>() {
    private lateinit var viewModel: CakeScriptConfigurationViewModel

    override fun resetEditorFrom(configuration: CakeScriptConfiguration) {
        viewModel.reset(configuration.state ?: CakeScriptConfigurationOptions())
    }

    override fun applyEditorTo(configuration: CakeScriptConfiguration) {
        viewModel.apply(configuration.state ?: return)
    }

    override fun createEditor(lifetime: Lifetime): JComponent {
        viewModel = CakeScriptConfigurationViewModel(lifetime, project)
        return ControlViewBuilder(lifetime, project, CakeScriptConfigurationType.id).customBuild(viewModel, project)
    }
}
