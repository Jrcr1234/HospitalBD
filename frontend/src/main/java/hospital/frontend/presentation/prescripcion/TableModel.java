package hospital.frontend.presentation.prescripcion;

import hospital.protocol.logic.LineaDetalle;
import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<LineaDetalle> rows;
    private final String[] cols = {"Código", "Medicamento", "Cantidad", "Indicaciones", "Duración (días)"};

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public TableModel(List<LineaDetalle> rows) { this.rows = rows; }
    public void setRows(List<LineaDetalle> rows) {
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
        LineaDetalle linea = rows.get(row);
        switch (col) {
            case 0: return linea.getMedicamento().getCodigo();
            case 1: return linea.getMedicamento().getNombre();
            case 2: return linea.getCantidad();
            case 3: return linea.getIndicaciones();
            case 4: return linea.getDuracionTratamiento();
            default: return "";
        }
    }
}