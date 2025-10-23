package hospital.frontend.presentation.mainview.historicoRecetas;

import hospital.protocol.logic.Receta;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Receta> rows;
    private final String[] cols;

    public TableModel() {
        this.cols = new String[]{"Código", "Paciente", "Médico", "Fecha", "Estado"};
        this.rows = new java.util.ArrayList<>();
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
            case 0: return receta.getCodigo();
            case 1: return (receta.getPaciente() != null) ? receta.getPaciente().getNombre() : "N/A";
            case 2: return (receta.getMedico() != null) ? receta.getMedico().getNombre() : "N/A";
            case 3: return (receta.getFechaConfeccion() != null) ? receta.getFechaConfeccion().toString() : "N/A";
            case 4: return (receta.getEstado() != null) ? receta.getEstado().name() : "N/A";
            default: return "";
        }
    }

    public void setList(List<Receta> rows) {
        this.rows = rows;
        // Notifica a la JTable que los datos han cambiado y debe redibujarse.
        this.fireTableDataChanged();
    }
}