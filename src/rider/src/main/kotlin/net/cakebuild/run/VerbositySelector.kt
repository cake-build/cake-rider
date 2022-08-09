package net.cakebuild.run

import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.IProperty
import com.jetbrains.rd.util.reactive.Property
import net.cakebuild.shared.ui.VerbosityComboBox
import javax.swing.JComponent

class VerbositySelector(lifetime: Lifetime, name: String, id: String) : CustomControlBase(lifetime, name, id) {
    override val valueCanBeReverted: IProperty<Boolean> = Property(false)

    val verbosity: IProperty<String> = Property(VerbosityComboBox.DEFAULT)

    override fun revert() {
        verbosity.set(VerbosityComboBox.DEFAULT)
    }

    override fun build(project: Project): JComponent {
        val verbosityComboBox = VerbosityComboBox()
        verbosity.advise(lifetime, verbosityComboBox::setVerbosity)
        verbosityComboBox.addItemListener {
            verbosity.set(verbosityComboBox.getVerbosity())
        }

        return makeLabeledComponent(verbosityComboBox)
    }
}