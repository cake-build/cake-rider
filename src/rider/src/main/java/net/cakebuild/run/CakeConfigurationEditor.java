package net.cakebuild.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.ui.components.fields.ExpandableTextField;
import net.cakebuild.shared.ui.VerbosityComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CakeConfigurationEditor extends SettingsEditor<CakeConfiguration> {
    private JPanel myPanel;
    private JTextField scriptPathField;
    private JTextField taskField;
    private JComboBox<String> verbosityBox;
    private JTextField argumentsField;

    @Override
    protected void resetEditorFrom(@NotNull CakeConfiguration configuration) {
        CakeConfigurationOptions state = configuration.getState();
        if(state == null) {
            state = new CakeConfigurationOptions(); // use defaults.
        }
        scriptPathField.setText(state.getScriptPath());
        taskField.setText(state.getTaskName());
        ((VerbosityComboBox)verbosityBox).setVerbosity(state.getVerbosity());
        argumentsField.setText((state.getAdditionalArguments()));
    }

    @Override
    protected void applyEditorTo(@NotNull CakeConfiguration configuration) throws ConfigurationException {
        CakeConfigurationOptions state = configuration.getState();
        if(state == null) {
            throw new ConfigurationException("State is null: can not set state.");
        }

        state.setScriptPath(scriptPathField.getText());
        state.setTaskName(taskField.getText());
        state.setVerbosity(((VerbosityComboBox)verbosityBox).getVerbosity());
        state.setAdditionalArguments(argumentsField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {
        verbosityBox = new VerbosityComboBox();
        argumentsField = new ExpandableTextField();
    }
}
