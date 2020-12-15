package net.cakebuild.settings;

import com.intellij.ide.ui.laf.darcula.DarculaUIUtil;
import net.cakebuild.shared.ui.RegexInputVerifier;

import javax.swing.*;
import java.util.Objects;

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

        verbosityBox.putClientProperty(DarculaUIUtil.COMPACT_PROPERTY, Boolean.TRUE);
        verbosityBox.addItem("Quiet");
        verbosityBox.addItem("Minimal");
        verbosityBox.addItem("Normal");
        verbosityBox.addItem("Verbose");
        verbosityBox.addItem("Diagnostic");
    }

    public JPanel getContent() { return myPanel; }

    public JTextField getFileExtensionField() { return fileExtensionField; }

    public JTextField getTaskRegexField() { return taskRegexField; }

    public String getVerbosity() { return ((String) Objects.requireNonNull(verbosityBox.getSelectedItem())).toLowerCase(); }
    public void setVerbosity(String value) {
        Object item = null;
        int count = verbosityBox.getItemCount();
        for(int i=0; i<count; i++){
            String o = verbosityBox.getItemAt(i);
            if(value.equals(o.toLowerCase())){
                item = o;
                break;
            }
        }

        if(null == item) {
            // add new..
            verbosityBox.addItem(value);
            item = value;
        }

        verbosityBox.setSelectedItem(item);
    }

    private void setValidationError(String error) {
        if(error == null) {
            validationErrors.setText("");
            return;
        }

        validationErrors.setText(error);
    }
}
