package hospital.frontend.presentation.prescripcion.medicamento_add;

import hospital.protocol.logic.Medicamento;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Medicamento> rows;
    private final String[] cols = {"Código", "Nombre", "Presentación"};

    public TableModel(List<Medicamento> rows) { this.rows = rows; }

    public void setRows(List<Medicamento> rows) {
        this.rows = rows;
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return rows.size(); }
    @Override
    public int getColumnCount() { return cols.length; }
    @Override
    public String getColumnName(int col) { return cols[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Medicamento med = rows.get(row);
        switch (col) {
            case 0: return med.getCodigo();
            case 1: return med.getNombre();
            case 2: return med.getPresentacion();
            default: return "";
        }
    }
}