package hospital.frontend.presentation.mainview.dashboard;

import hospital.protocol.logic.Medicamento;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Medicamento> rows;
    private final String[] cols = {"Medicamento Seleccionado"};

    public TableModel(List<Medicamento> rows) {
        this.rows = rows;
    }

    public void setRows(List<Medicamento> rows) {
        this.rows = rows;
        this.fireTableDataChanged(); // Notifica a la JTable que los datos cambiaron
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int col) {
        return cols[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Medicamento med = rows.get(row);
        // Como solo hay una columna, siempre devolvemos el nombre del medicamento.
        return med.getNombre();
    }
}