package hospital.frontend.presentation.prescripcion.dosis_details;

import hospital.frontend.presentation.util.GuiUtils;
import hospital.protocol.logic.LineaDetalle;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// La vista ahora implementa PropertyChangeListener para escuchar al modelo
public class View implements PropertyChangeListener {
    private JPanel panel;
    private JComboBox<Integer> cantidadCmb;
    private JComboBox<Integer> duracionCmb;
    private JTextArea indicacionesFld;
    private JButton guardarBtn;
    private JButton cancelarBtn;

    private Controller controller;
    private Model model;

    public View() {}

    public void init() {
        // --- Asignación de Iconos ---
        try {
            int iconSize = 24;
            guardarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/guardar.png")), iconSize, iconSize));
            cancelarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/cancel.png")), iconSize, iconSize));
        } catch (Exception e) {
            System.err.println("Error al cargar los iconos de los botones: " + e.getMessage());
        }

        // --- ActionListeners ---
        guardarBtn.addActionListener(e -> controller.guardar());
        cancelarBtn.addActionListener(e -> controller.cancelar());

        // Llenado de ComboBoxes
        for (int i = 1; i <= 10; i++) {
            cantidadCmb.addItem(i);
        }
        for (int i = 1; i <= 30; i++) {
            duracionCmb.addItem(i);
        }
    }

    // --- Getters para que el Controller lea los datos ---
    // Ahora son más robustos y pueden lanzar una excepción si los datos son inválidos
    public int getCantidad() throws Exception {
        Object itemSeleccionado = cantidadCmb.getSelectedItem();
        if (itemSeleccionado == null) {
            throw new Exception("Debe seleccionar una cantidad.");
        }
        return (Integer) itemSeleccionado;
    }

    public int getDuracion() throws Exception {
        Object itemSeleccionado = duracionCmb.getSelectedItem();
        if (itemSeleccionado == null) {
            throw new Exception("Debe seleccionar una duración.");
        }
        return (Integer) itemSeleccionado;
    }

    public String getIndicaciones() { return indicacionesFld.getText().trim(); }

    // --- Métodos de enlace MVC ---
    public JPanel getPanel() { return panel; }
    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        // La vista se suscribe a los cambios del modelo
        model.addPropertyChangeListener(this);

        // Rellenamos los datos si estamos en modo de edición
        if (model.getLineaExistente() != null) {
            LineaDetalle linea = model.getLineaExistente();
            cantidadCmb.setSelectedItem(linea.getCantidad());
            duracionCmb.setSelectedItem(linea.getDuracionTratamiento());
            indicacionesFld.setText(linea.getIndicaciones());
        }
    }

    // === PROPERTYCHANGE AHORA MANEJA LOS ERRORES ===
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Escucha por errores y los muestra
        if ("errorMessage".equals(evt.getPropertyName())) {
            String error = (String) evt.getNewValue();
            if (error != null && !error.isEmpty()) {
                JOptionPane.showMessageDialog(panel, error, "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}