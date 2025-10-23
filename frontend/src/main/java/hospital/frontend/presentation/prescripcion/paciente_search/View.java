package hospital.frontend.presentation.prescripcion.paciente_search;

import hospital.frontend.presentation.util.GuiUtils;
import hospital.protocol.logic.Paciente;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {
    private JPanel panel;
    private JComboBox<String> filtroCmb;
    private JTextField searchFld;
    private JTable pacientesTbl;
    private JButton cancelarBtn;
    private JButton seleccionarBtn;

    private Controller controller;
    private Model model;
    private TableModel tableModel;

    public View() {
        // Constructor vacío
    }

    public void init() {
        // --- Asignación de Iconos ---
        try {
            int iconSize = 24;
            seleccionarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/ok.png")), iconSize, iconSize));
            cancelarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/cancel.png")), iconSize, iconSize));
        } catch (Exception e) {
            System.err.println("Error al cargar los iconos de los botones: " + e.getMessage());
        }

        filtroCmb.setModel(new DefaultComboBoxModel<>(new String[]{"Nombre", "Cédula"}));

        // --- ActionListeners Simplificados ---
        seleccionarBtn.addActionListener(e -> controller.seleccionar());
        cancelarBtn.addActionListener(e -> controller.cancelar());

        // --- Lógica de búsqueda automática ---
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { controller.buscar(); }
            public void removeUpdate(DocumentEvent e) { controller.buscar(); }
            public void changedUpdate(DocumentEvent e) {}
        };
        searchFld.getDocument().addDocumentListener(listener);
        filtroCmb.addActionListener(e -> controller.buscar());
    }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        tableModel = new TableModel(model.getList());
        pacientesTbl.setModel(tableModel);
    }

    // === PROPERTYCHANGE AHORA MANEJA TODAS LAS ACTUALIZACIONES ===
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        // 1. Escucha por errores y los muestra
        if ("errorMessage".equals(evt.getPropertyName())) {
            String error = (String) evt.getNewValue();
            if (error != null && !error.isEmpty()) {
                JOptionPane.showMessageDialog(panel, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 2. Escucha por cambios en la lista y actualiza la tabla
        if (Model.LIST.equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(() -> {
                tableModel.setRows(model.getList());
                panel.revalidate();
            });
        }
    }

    // --- Métodos de interacción para el Controller (sin cambios) ---
    public JPanel getPanel() { return panel; }
    public String getFiltro() { return (String) filtroCmb.getSelectedItem(); }
    public String getTerminoBusqueda() { return searchFld.getText(); }

    public Paciente getSelectedPatient() {
        int selectedRow = pacientesTbl.getSelectedRow();
        if (selectedRow >= 0) {
            return model.getList().get(selectedRow);
        }
        return null;
    }
}