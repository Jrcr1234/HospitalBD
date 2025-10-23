package hospital.frontend.presentation.mainview.medicos;

import hospital.protocol.logic.Medico;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Medico> rows;
    private final String[] cols = {"Cédula", "Nombre", "Especialidad"};

    public TableModel(List<Medico> rows) {
        this.rows = rows;
    }

    public void setRows(List<Medico> rows) {
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
        Medico med = rows.get(row);
        switch (col) {
            case 0: return med.getId();
            case 1: return med.getNombre();
            case 2: return med.getEspecialidad();
            default: return "";
        }
    }
}