package net.cakebuild.run

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.util.ui.UIUtil
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.compose
import com.jetbrains.rider.run.configurations.controls.AnchoredPanel
import com.jetbrains.rider.run.configurations.controls.ControlBase
import com.jetbrains.rider.run.configurations.controls.ControlViewBuilder
import com.jetbrains.rider.run.configurations.controls.IRevertableControl
import com.jetbrains.rider.run.configurations.controls.RevertableLabeledComponent
import com.jetbrains.rider.run.configurations.controls.RunConfigurationViewModelBase
import com.jetbrains.rider.run.configurations.controls.VisibilityMode
import javax.swing.JComponent

abstract class CustomControlBase(override val lifetime: Lifetime, name: String, id: String) :
    ControlBase(name, id), IRevertableControl {
    abstract fun build(project: Project): JComponent

    protected fun <T : JComponent> makeLabeledComponent(component: T): RevertableLabeledComponent<T> {
        return RevertableLabeledComponent(LabeledComponent.create(component, name), this).also { labeledComponent ->
            labeledComponent.setLabelLocation("West")

            this.isEnabled
                .compose(this.visibilityMode) { a, b -> Pair(a, b) }
                .advise(lifetime) { (isEnabled, visibilityMode) ->
                    when (visibilityMode) {
                        VisibilityMode.InvisibleHidden -> {
                            labeledComponent.isVisible = isEnabled
                            component.isVisible = isEnabled
                        }

                        VisibilityMode.InvisibleCollapsed -> {
                            labeledComponent.isVisible = true
                            component.isVisible = isEnabled
                        }
                    }
                }

            this.isEnabled.advise(lifetime) {
                labeledComponent.isEnabled = it
            }
        }
    }
}

/**
 * Wrapper for [ControlViewBuilder.build] to allow for custom controls
 */
fun ControlViewBuilder.customBuild(
    viewModel: RunConfigurationViewModelBase,
    project: Project,
): AnchoredPanel {
    val controls = viewModel.controls as MutableList<ControlBase>
    val injects = mutableMapOf<Int, CustomControlBase>()

    // Remove custom controls and save their indexes to add them later, based on MutableList.removeAll
    controls.apply {
        var writeIndex = 0
        for (readIndex in 0..lastIndex) {
            val element = this[readIndex]
            if (element is CustomControlBase) {
                injects[readIndex] = element
                continue
            }

            if (writeIndex != readIndex) {
                this[writeIndex] = element
            }

            writeIndex++
        }
        if (writeIndex < size) {
            for (removeIndex in lastIndex downTo writeIndex) {
                removeAt(removeIndex)
            }
        }
    }

    return this.build(viewModel).apply {
        for (inject in injects) {
            add(inject.value.build(project), inject.key)
        }

        // This makes everything aligned nicely
        UIUtil.mergeComponentsWithAnchor(this.components.filterIsInstance<RevertableLabeledComponent<*>>())
    }
}
