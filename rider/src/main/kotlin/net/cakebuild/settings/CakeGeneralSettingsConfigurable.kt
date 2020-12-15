package net.cakebuild.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import java.util.regex.PatternSyntaxException
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class CakeGeneralSettingsConfigurable(private val project: Project) : Configurable {
    private val editor = CakeGeneralSettingsEditor()

    // apply ui to settings
    override fun apply() {
        // validate: Throw ConfigurationException on validation-errors works in IntelliJ,
        // however it does not work in rider: https://youtrack.jetbrains.com/issue/RIDER-55231
        try {
            Regex(editor.taskRegexField.text)
        } catch (e: PatternSyntaxException) {
            throw ConfigurationException("${e.javaClass.name}: ${e.message}", e, "Task Regex:")
        }

        // real apply
        val settings = CakeSettings.getInstance(project)
        settings.cakeFileExtension = editor.fileExtensionField.text
        settings.cakeTaskParsingRegex = editor.taskRegexField.text
        settings.cakeVerbosity = editor.verbosity
    }

    override fun createComponent(): JComponent? {
        return editor.content
    }

    override fun isModified(): Boolean {
        val settings = CakeSettings.getInstance(project)
        val pairs = arrayOf(
            Pair({ settings.cakeFileExtension }, { editor.fileExtensionField }),
            Pair({ settings.cakeTaskParsingRegex }, { editor.taskRegexField }),
        )
        val modified = pairs.any {
            isModified(it.second(), it.first())
        }
        if (modified) {
            return true
        }

        return settings.cakeVerbosity != editor.verbosity
    }

    // reset ui from settings
    override fun reset() {
        val settings = CakeSettings.getInstance(project)
        editor.fileExtensionField.text = settings.cakeFileExtension
        editor.taskRegexField.text = settings.cakeTaskParsingRegex
        editor.verbosity = settings.cakeVerbosity
    }

    override fun getDisplayName(): String {
        return "Cake"
    }
}
