package net.cakebuild.shared.ui

import javax.swing.DefaultCellEditor
import javax.swing.JTextField

class RegexCellEditor : DefaultCellEditor(JTextField()) {

    private val verifier = RegexInputVerifier()

    var onValidationSuccess: (() -> Unit)?
        get() { return verifier.onValidationSuccess }
        set(value) { verifier.onValidationSuccess = value }

    var onValidationError: ((String) -> Unit)?
        get() { return verifier.onValidationError }
        set(value) { verifier.onValidationError = value }

    init {
        component.inputVerifier = verifier
    }

    override fun stopCellEditing(): Boolean {
        val valid = verifier.shouldYieldFocus(component)
        if (!valid) return false

        return super.stopCellEditing()
    }

    override fun getComponent(): JTextField {
        return super.getComponent() as JTextField
    }
}
