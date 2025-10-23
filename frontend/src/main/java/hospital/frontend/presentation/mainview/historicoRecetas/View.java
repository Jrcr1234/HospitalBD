package hospital.frontend.presentation.mainview.historicoRecetas;

import hospital.frontend.presentation.util.GuiUtils;
import hospital.protocol.logic.Receta;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {
    private JPanel panel;
    private JTable tablaRecetas;
    private JTextField buscarTextField;
    private JButton buscarButton;
    private JButton detalleButton;
    private TableModel tableModel; // Usaremos un TableModel personalizado

    private Controller controller;
    private Model model;

    public View() {
        // Constructor vacío
    }

    public void init() {

        // --- Action Listeners Simplificados ---
        buscarButton.addActionListener(e -> controller.search(buscarTextField.getText()));

        detalleButton.addActionListener(e -> {
            int selectedRow = tablaRecetas.getSelectedRow();
            if (selectedRow != -1) {
                controller.verDetalle(selectedRow);
            }
        });

        // --- Asignación de Iconos ---
        try {
            int iconSize = 24;
            buscarButton.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/buscar.png")), iconSize, iconSize));
        } catch (Exception e) {
            System.err.println("Error al cargar icono de búsqueda: " + e.getMessage());
        }
    }

    // === Integración con MVC ===
    public JPanel getPanel() {
        return panel;
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this); // La vista se suscribe a los cambios

        // Inicializamos el TableModel personalizado
        this.tableModel = new TableModel();
        this.tablaRecetas.setModel(tableModel);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // === PROPERTYCHANGE AHORA MANEJA TODAS LAS ACTUALIZACIONES ===
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        // 1. Escucha por errores y los muestra
        if (evt.getPropertyName().equals("errorMessage")) {
            String error = (String) evt.getNewValue();
            if (error != null && !error.isEmpty()) {
                JOptionPane.showMessageDialog(panel, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 2. Escucha por cambios en la lista y actualiza la tabla
        if (evt.getPropertyName().equals("list")) {
            SwingUtilities.invokeLater(() -> {
                tableModel.setList(model.getList());
                panel.revalidate();
            });
        }

        // 3. Escucha por un cambio en la receta actual y muestra los detalles
        if (evt.getPropertyName().equals("current")) {
            Receta receta = model.getCurrent();
            // Solo muestra el diálogo si la receta no está vacía (es decir, se ha seleccionado una)
            if (receta != null && receta.getCodigo() != null && !receta.getCodigo().isEmpty()) {
                mostrarDetalles(receta);
            }
        }
    }

    // --- MÉTODO PRIVADO PARA MOSTRAR DETALLES ---
    // La Vista es ahora la responsable de construir y mostrar este diálogo.
    private void mostrarDetalles(Receta receta) {
        StringBuilder detalles = new StringBuilder();
        detalles.append("<html><b>Código:</b> ").append(receta.getCodigo()).append("<br>");
        detalles.append("<b>Paciente:</b> ").append(
                receta.getPaciente() != null ? receta.getPaciente().getNombre() : "No asignado").append("<br>");
        detalles.append("<b>Médico:</b> ").append(
                receta.getMedico() != null ? receta.getMedico().getNombre() : "No asignado").append("<br>");
        detalles.append("<b>Fecha Confección:</b> ").append(
                receta.getFechaConfeccion() != null ? receta.getFechaConfeccion().toString() : "N/A").append("<br>");
        detalles.append("<b>Fecha Retiro:</b> ").append(
                receta.getFechaRetiro() != null ? receta.getFechaRetiro().toString() : "N/A").append("<br>");
        detalles.append("<b>Estado:</b> ").append(receta.getEstado() != null ? receta.getEstado().name() : "N/A").append("<br><br>");
        detalles.append("<b>Medicamentos:</b><br>");
        receta.getLineasDetalle().forEach(ld ->
                detalles.append("&nbsp;-&nbsp;").append(ld.getMedicamento().getNombre())
                        .append(" | Cantidad: ").append(ld.getCantidad())
                        .append(" | Indicación: ").append(ld.getIndicaciones())
                        .append("<br>")
        );
        detalles.append("</html>");

        JOptionPane.showMessageDialog(panel, detalles.toString(),
                "Detalle de Receta", JOptionPane.INFORMATION_MESSAGE);
    }
}