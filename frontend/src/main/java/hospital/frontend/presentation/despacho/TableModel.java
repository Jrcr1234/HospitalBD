package hospital.frontend.presentation.despacho;

import hospital.protocol.logic.Receta;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TableModel extends AbstractTableModel {
    private List<Receta> rows;
    private final String[] cols = {"Código", "Paciente", "Fecha Retiro", "Estado"};
    // --- Añadir el formateador ---
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public TableModel(List<Receta> rows) {
        this.rows = rows;
    }

    public void setRows(List<Receta> rows) {
        this.rows = rows;
        this.fireTableDataChanged();
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
        Receta receta = rows.get(row);
        switch (col) {
            case 0:
                return receta.getCodigo();
            case 1:
                return (receta.getPaciente() != null) ? receta.getPaciente().getNombre() : "(Paciente no encontrado)";

            // === Aplicar formato a la Fecha Retiro ===
            case 2:
                Date fechaRet = receta.getFechaRetiro();
                return (fechaRet != null) ? sdf.format(fechaRet) : "(Sin fecha)";
            // =======================================

            case 3:
                return (receta.getEstado() != null) ? receta.getEstado().name() : "(Sin estado)";
            default:
                return "";
        }
    }
}