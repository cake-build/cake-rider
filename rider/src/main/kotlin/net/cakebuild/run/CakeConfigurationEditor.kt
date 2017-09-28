package net.cakebuild.run

import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.layout.panel
import javax.swing.JComponent

class CakeConfigurationEditor : SettingsEditor<CakeConfiguration>() {
    override fun applyEditorTo(configuration: CakeConfiguration) {
    }

    override fun resetEditorFrom(configuration: CakeConfiguration) {
    }

    override fun createEditor(): JComponent {
        return panel {
            row {
                label("Cool GUI, huh?")
            }
        }
    }
}