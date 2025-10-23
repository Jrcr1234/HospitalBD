package hospital.frontend.presentation.despacho;

import hospital.protocol.logic.Receta;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Receta> rows;
    private final String[] cols = {"Código", "Paciente", "Fecha Retiro", "Estado"};

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
                // Verificamos si el paciente existe
                if (receta.getPaciente() != null) {
                    return receta.getPaciente().getNombre();
                } else {
                    return "(Paciente no encontrado)";
                }

            case 2:
                // Verificamos si la fecha existe
                if (receta.getFechaRetiro() != null) {
                    return receta.getFechaRetiro().toString();
                } else {
                    return "(Sin fecha)";
                }

            case 3:
                // Verificamos si el estado existe
                if (receta.getEstado() != null) {
                    return receta.getEstado().name();
                } else {
                    return "(Sin estado)"; // Mensaje de advertencia
                }
                // ======== FIN DE LA NUEVA CORRECCIÓN ========

            default:
                return "";
        }
    }
}