package net.cakebuild.run.frosting

import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rider.run.configurations.LifetimedSettingsEditor
import com.jetbrains.rider.run.configurations.controls.ControlViewBuilder
import net.cakebuild.run.customBuild
import javax.swing.JComponent

class CakeFrostingConfigurationEditor(val project: Project) : LifetimedSettingsEditor<CakeFrostingConfiguration>() {
    private lateinit var viewModel: CakeFrostingConfigurationViewModel

    override fun resetEditorFrom(configuration: CakeFrostingConfiguration) {
        viewModel.reset(configuration.parameters)
    }

    override fun applyEditorTo(configuration: CakeFrostingConfiguration) {
        viewModel.apply(configuration.parameters)
    }

    override fun createEditor(lifetime: Lifetime): JComponent {
        viewModel = CakeFrostingConfigurationViewModel(lifetime, project)
        return ControlViewBuilder(lifetime, project, CakeFrostingConfigurationType.id).customBuild(viewModel, project)
    }
}
