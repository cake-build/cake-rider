package net.cakebuild.run

import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.layout.panel
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class CakeConfigurationEditor : SettingsEditor<CakeConfiguration>() {

    private val myPanel: JPanel
    private val scriptPath = JTextField("")
    private val task = JTextField("")
    private val verbosity = JTextField("")

    init {
        myPanel = panel {
            row("Script path") {
                scriptPath(grow)
            }
            row("Task") {
                task(grow)
            }
            row("Verbosity") {
                verbosity(grow)
            }
        }

        // todo: validate verbosity and path?
    }

    override fun applyEditorTo(configuration: CakeConfiguration) {
        configuration.setOptions(scriptPath.text, task.text, verbosity.text)
    }

    override fun resetEditorFrom(configuration: CakeConfiguration) {
        val state = configuration.state!!
        scriptPath.text = state.scriptPath ?: ""
        task.text = state.taskName ?: ""
        verbosity.text = state.verbosity ?: ""
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}