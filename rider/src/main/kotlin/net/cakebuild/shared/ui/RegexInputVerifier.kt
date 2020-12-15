package net.cakebuild.shared.ui

import java.util.regex.PatternSyntaxException
import javax.swing.InputVerifier
import javax.swing.JComponent
import javax.swing.JTextField

class RegexInputVerifier : InputVerifier() {

    var onValidationSuccess: (() -> Unit)? = null
    var onValidationError: ((String) -> Unit)? = null
    private var lastErrorMessage = ""

    override fun verify(input: JComponent?): Boolean {
        val text = (input as JTextField).text
        try {
            Regex(text)
        } catch (e: PatternSyntaxException) {
            lastErrorMessage = "${e.javaClass.name}: ${e.message}"
            return false
        }

        return true
    }

    override fun shouldYieldFocus(input: JComponent?): Boolean {
        val valid = verify(input)
        if (valid) {
            input?.putClientProperty("JComponent.outline", null)
            onValidationSuccess?.invoke()
        } else {
            input?.putClientProperty("JComponent.outline", "error")
            onValidationError?.invoke(lastErrorMessage)
        }
        input?.repaint()
        return valid
    }
}
