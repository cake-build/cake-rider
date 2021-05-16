package net.cakebuild.settings;

import net.cakebuild.shared.ui.RegexCellEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

public class CakeRunnerSettingsEditor {
    private JPanel content;
    private JTextField cakeRunnerField;
    private JTable overrides;
    private JButton addButton;
    private JButton removeButton;
    private JLabel errorText;
    private JLabel tableLabel;
    private final DefaultTableModel model;

    public CakeRunnerSettingsEditor() {
        model = (DefaultTableModel) overrides.getModel();
        model.addColumn("OS Regex");
        model.addColumn("Override");
        overrides.setModel(model);
        overrides.getColumnModel().getColumn(0).setHeaderValue("os.name RegEx");
        overrides.getColumnModel().getColumn(1).setHeaderValue("Tool");
        overrides.putClientProperty("terminateEditOnFocusLost", true);
        overrides.setCellSelectionEnabled(false);
        overrides.setRowSelectionAllowed(true);
        overrides.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        overrides.getSelectionModel().addListSelectionListener(e -> {
            int row = overrides.getSelectedRow();
            removeButton.setEnabled(row >= 0);
        });
        RegexCellEditor regexCellEditor = new RegexCellEditor();
        regexCellEditor.setOnValidationError(s -> { setValidationError(s); return null; });
        regexCellEditor.setOnValidationSuccess(() -> { setValidationError(null); return null; });
        overrides.getColumnModel().getColumn(0).setCellEditor(regexCellEditor);
        addButton.addActionListener(e -> {
            model.addRow(new Object[]{"",""});
            int row = model.getRowCount()-1;
            overrides.editCellAt(row, 0);
            overrides.changeSelection(row, 0, false, false);
            TableCellEditor editor = overrides.getCellEditor(row, 0);
            Component textEdit = overrides.prepareEditor(editor, row, 0);
            textEdit.requestFocus();
        });
        removeButton.setEnabled(false);
        removeButton.addActionListener(e -> {
            int row = overrides.getSelectedRow();
            if(row < 0) {
                return;
            }

            model.removeRow(row);
        });
        tableLabel.setToolTipText("Current os.name: "+System.getProperty("os.name"));
    }

    public JPanel getContent() { return content; }

    public JTextField getCakeRunnerField() { return cakeRunnerField; }

    public Map<String, String> getCakeRunnerOverrides() {
        Map<String, String> overrides = new Hashtable<>();
        int rows = model.getRowCount();
        for(int i =0; i < rows; i++) {
            String regex = (String)model.getValueAt(i, 0);
            String override = (String)model.getValueAt(i, 1);

            overrides.put(regex, override);
        }

        return overrides;
    }

    public void setCakeRunnerOverrides(Map<String, String> overrides) {
        while (model.getRowCount() > 0){
            model.removeRow(0);
        }
        overrides.forEach((regex, override) -> model.addRow(new Object[]{regex, override}));
    }

    private void setValidationError(String error) {
        if(error == null) {
            errorText.setText("");
            return;
        }

        errorText.setText(error);
    }
}
