package net.cakebuild.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class CakeRunnerSettingsConfigurable(private val project: Project) : Configurable {
    private val editor = CakeRunnerSettingsEditor()

    // apply ui to settings
    override fun apply() {
        val settings = CakeSettings.getInstance(project)
        settings.cakeRunner = editor.cakeRunnerField.text
        settings.cakeRunnerOverrides = editor.cakeRunnerOverrides
        settings.cakeUseNetTool = editor.useNetTool.isSelected
    }

    override fun createComponent(): JComponent? {
        return editor.content
    }

    @Suppress("DuplicatedCode")
    override fun isModified(): Boolean {
        val settings = CakeSettings.getInstance(project)
        val pairs = arrayOf(
            Pair({ settings.cakeRunner }, { editor.cakeRunnerField })
        )
        val textFieldsModified = pairs.any {
            Configurable.isFieldModified(it.second(), it.first())
        }

        if (textFieldsModified) {
            return true
        }

        if (isModified(settings.cakeRunnerOverrides, editor.cakeRunnerOverrides)) {
            return true
        }

        if (Configurable.isCheckboxModified(editor.useNetTool, settings.cakeUseNetTool)) {
            return true
        }
        return false
    }

    // reset ui from settings
    override fun reset() {
        val settings = CakeSettings.getInstance(project)
        editor.cakeRunnerField.text = settings.cakeRunner
        editor.cakeRunnerOverrides = settings.cakeRunnerOverrides
        editor.useNetTool.isSelected = settings.cakeUseNetTool
        editor.updateEnabledState()
    }

    override fun getDisplayName(): String {
        return "Runner"
    }

    private fun isModified(left: Map<String, String>, right: Map<String, String>): Boolean {
        if (left.size != right.size) {
            return true
        }

        left.entries.forEach {
            val rightVal = right[it.key] ?: return true
            if (it.value != rightVal) {
                return true
            }
        }

        return false
    }
}
