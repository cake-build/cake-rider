package net.cakebuild.settings;

import com.intellij.ide.ui.laf.darcula.DarculaUIUtil;
import net.cakebuild.shared.ui.RegexInputVerifier;
import net.cakebuild.shared.ui.VerbosityComboBox;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CakeGeneralSettingsEditor {

    private JPanel myPanel;
    private JTextField fileExtensionField;
    private JTextField taskRegexField;
    private JLabel validationErrors;
    private JComboBox<String> verbosityBox;

    public CakeGeneralSettingsEditor() {
        taskRegexField.putClientProperty(DarculaUIUtil.COMPACT_PROPERTY, Boolean.TRUE);
        RegexInputVerifier verifier = new RegexInputVerifier();
        verifier.setOnValidationError((s) -> { setValidationError(s); return null; });
        verifier.setOnValidationSuccess(() -> { setValidationError(null); return null; });
        taskRegexField.setInputVerifier(verifier);
    }

    public JPanel getContent() { return myPanel; }

    public JTextField getFileExtensionField() { return fileExtensionField; }

    public JTextField getTaskRegexField() { return taskRegexField; }

    public String getVerbosity() { return ((VerbosityComboBox)verbosityBox).getVerbosity(); }
    public void setVerbosity(String value) { ((VerbosityComboBox)verbosityBox).setVerbosity(value); }

    private void setValidationError(String error) {
        if(error == null) {
            validationErrors.setText("");
            return;
        }

        validationErrors.setText(error);
    }

    private void createUIComponents() {
        verbosityBox = new VerbosityComboBox();
    }
}
