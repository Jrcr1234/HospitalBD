package hospital.frontend.presentation.prescripcion.paciente_search;

import hospital.protocol.logic.Paciente;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Paciente> rows;
    private final String[] cols = {"Cédula", "Nombre"};

    public TableModel(List<Paciente> rows) { this.rows = rows; }
    public void setRows(List<Paciente> rows) {
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
        Paciente p = rows.get(row);
        switch (col) {
            case 0: return p.getId();
            case 1: return p.getNombre();
            default: return "";
        }
    }
}