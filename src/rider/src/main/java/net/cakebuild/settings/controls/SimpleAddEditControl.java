package net.cakebuild.settings.controls;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;

public class SimpleAddEditControl {
    private JTable table;
    private JButton addItem;
    private JButton removeItem;
    private JPanel panel;
    private JLabel errorText;
    private final DefaultTableModel model;

    public SimpleAddEditControl(String[] headers, Supplier<String[]> newItemGenerator){
        model = (DefaultTableModel) table.getModel();
        for(int i = 0; i< headers.length; i++){
            model.addColumn("col"+i);
        }
        TableColumnModel columnModel = table.getColumnModel();
        for(int i = 0; i< headers.length; i++){
            columnModel.getColumn(i).setHeaderValue(headers[i]);
        }
        table.putClientProperty("terminateEditOnFocusLost", true);
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            removeItem.setEnabled(row >= 0);
        });
        addItem.addActionListener(e -> {
            model.addRow(newItemGenerator.get());
            int row = model.getRowCount()-1;
            table.editCellAt(row, 0);
            table.changeSelection(row, 0, false, false);
            TableCellEditor editor = table.getCellEditor(row, 0);
            Component textEdit = table.prepareEditor(editor, row, 0);
            textEdit.requestFocus();
        });
        removeItem.setEnabled(false);
        removeItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row < 0) {
                return;
            }

            model.removeRow(row);
        });
    }

    public void setCellEditor(int columnIndex, TableCellEditor cellEditor){
        table.getColumnModel().getColumn(columnIndex).setCellEditor(cellEditor);
    }

    public Collection<String> getData() {
        Collection<String> items = new HashSet<>();
        int rows = model.getRowCount();
        for(int i =0; i < rows; i++) {
            String item = (String)model.getValueAt(i, 0);

            items.add(item);
        }

        return items;
    }

    public void setData(Collection<String> data) {
        while (model.getRowCount() > 0){
            model.removeRow(0);
        }
        data.forEach(it -> model.addRow(new Object[]{ it }));
    }

    public void setValidationError(String error) {
        if(error == null) {
            errorText.setText("");
            return;
        }

        errorText.setText(error);
    }

    public JPanel getContent() { return panel; }
}
