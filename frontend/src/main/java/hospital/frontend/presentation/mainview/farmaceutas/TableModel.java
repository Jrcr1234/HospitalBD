package hospital.frontend.presentation.mainview.farmaceutas;

import hospital.protocol.logic.Farmaceuta;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Farmaceuta> rows;
    private final String[] cols = {"Cédula", "Nombre"};

    public TableModel(List<Farmaceuta> rows) { this.rows = rows; }
    public void setRows(List<Farmaceuta> rows) {
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
        Farmaceuta far = rows.get(row);
        switch (col) {
            case 0: return far.getId();
            case 1: return far.getNombre();
            default: return "";
        }
    }
}