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
    }

    override fun createComponent(): JComponent? {
        return editor.content
    }

    override fun isModified(): Boolean {
        val settings = CakeSettings.getInstance(project)
        val pairs = arrayOf(
            Pair({ settings.cakeRunner }, { editor.cakeRunnerField })
        )
        val textFieldsModified = pairs.any {
            isModified(it.second(), it.first())
        }

        if (textFieldsModified) {
            return true
        }

        if (isModified(settings.cakeRunnerOverrides, editor.cakeRunnerOverrides)) {
            return true
        }
        return false
    }

    // reset ui from settings
    override fun reset() {
        val settings = CakeSettings.getInstance(project)
        editor.cakeRunnerField.text = settings.cakeRunner
        editor.cakeRunnerOverrides = settings.cakeRunnerOverrides
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
