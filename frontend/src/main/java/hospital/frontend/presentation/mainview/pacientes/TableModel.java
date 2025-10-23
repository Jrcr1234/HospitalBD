package hospital.frontend.presentation.mainview.pacientes;

import hospital.protocol.logic.Paciente;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.text.SimpleDateFormat;

public class TableModel extends AbstractTableModel {
    private List<Paciente> rows;
    private final String[] cols = {"Cédula", "Nombre", "Teléfono", "Fecha de Nacimiento"};

    public TableModel(List<Paciente> rows) {
        this.rows = rows;
    }
    public void setRows(List<Paciente> rows) {
        this.rows = rows;
        // Esta es la señal oficial para que la JTable se redibuje completamente
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
        Paciente paciente = rows.get(row);
        switch (col) {
            case 0: return paciente.getId();
            case 1: return paciente.getNombre();
            case 2: return paciente.getTelefono();
            case 3:
                // Damos formato a la fecha para que se vea bien en la tabla
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                return sdf.format(paciente.getFechaNacimiento());
            default: return "";
        }
    }
}