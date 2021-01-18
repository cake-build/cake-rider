package net.cakebuild.settings;

import net.cakebuild.settings.controls.SimpleAddEditControl;
import net.cakebuild.shared.ui.RegexCellEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class CakeSearchPathSettingsEditor {
    private JPanel content;
    private JPanel searchPathsPanel;
    private JPanel excludesPanel;
    private SimpleAddEditControl mySearchPathsPanel;
    private SimpleAddEditControl myExcludesPanel;

    public JPanel getContent() { return content; }

    public void setScriptSearchPaths(@NotNull Collection<String> paths){
        mySearchPathsPanel.setData(paths);
    }

    public Collection<String> getScriptSearchPaths() {
        return mySearchPathsPanel.getData();
    }

    public void setScriptSearchIgnores(@NotNull Collection<String> expressions) {
        myExcludesPanel.setData(expressions);
    }

    public Collection<String> getScriptSearchIgnores() {
        return myExcludesPanel.getData();
    }

    private void createUIComponents() {
        mySearchPathsPanel = new SimpleAddEditControl(new String[] { "Path" }, () -> new String[] { "" } );
        searchPathsPanel = mySearchPathsPanel.getContent();

        myExcludesPanel = new SimpleAddEditControl(new String[] { "Exclude Regex" }, () -> new String[] { "" });
        excludesPanel = myExcludesPanel.getContent();
        RegexCellEditor regexCellEditor = new RegexCellEditor();
        regexCellEditor.setOnValidationError(s -> { myExcludesPanel.setValidationError(s); return null; });
        regexCellEditor.setOnValidationSuccess(() -> { myExcludesPanel.setValidationError(null); return null; });
        myExcludesPanel.setCellEditor(0, regexCellEditor);
    }
}
