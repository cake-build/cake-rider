package net.cakebuild.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class CakeSearchPathSettingsConfigurable(private val project: Project) : Configurable {
    private val editor = CakeSearchPathSettingsEditor()

    override fun createComponent(): JComponent? {
        return editor.content
    }

    override fun getDisplayName(): String {
        return "Search Paths"
    }

    override fun apply() {
        val settings = CakeSettings.getInstance(project)
        settings.cakeScriptSearchPaths = editor.scriptSearchPaths.toList()
        settings.cakeScriptSearchIgnores = editor.scriptSearchIgnores.toList()
    }

    override fun reset() {
        val settings = CakeSettings.getInstance(project)
        editor.scriptSearchPaths = settings.cakeScriptSearchPaths.toMutableList()
        editor.scriptSearchIgnores = settings.cakeScriptSearchIgnores.toMutableList()
    }

    override fun isModified(): Boolean {
        val settings = CakeSettings.getInstance(project)
        return isModified(editor.scriptSearchPaths, settings.cakeScriptSearchPaths) ||
            isModified(editor.scriptSearchIgnores, settings.cakeScriptSearchIgnores)
    }

    private fun isModified(left: Collection<String>, right: Collection<String>): Boolean {
        if (left.size != right.size) {
            return true
        }

        left.forEach {
            if (!right.contains(it)) {
                return true
            }
        }

        return false
    }
}
