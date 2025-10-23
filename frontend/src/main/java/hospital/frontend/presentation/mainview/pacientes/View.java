package hospital.frontend.presentation.mainview.pacientes;

import hospital.frontend.presentation.util.GuiUtils;
import hospital.protocol.logic.Paciente;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class View implements PropertyChangeListener {
    // --- Atributos de la Interfaz (sin cambios) ---
    private JPanel panel;
    private JPanel formularioPanel;
    private JPanel busquedaPanel;
    private JPanel tablaPanel;
    private JTable pacientesTbl;
    private JTextField idFld;
    private JTextField nombreFld;
    private JTextField telefonoFld;
    private JPanel dateChooserPanel;
    private com.toedter.calendar.JDateChooser fechaNacimientoFld;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JButton buscarBtn;
    private JTextField buscarFld;
    private JButton limpiarBtn;
    private JButton reporteBtn;

    // --- Variables para el MVC (sin cambios) ---
    private Controller controller;
    private Model model;
    private TableModel pacientesTableModel;

    public View() {
        // Constructor vacío
    }

    public void init() {
        // --- Asignación de Iconos (sin cambios) ---
        try {
            int iconSize = 24;
            guardarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/guardar.png")), iconSize, iconSize));
            borrarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/Cancelar.png")), iconSize, iconSize));
            limpiarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/limpiar.png")), iconSize, iconSize));
            buscarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/buscar.png")), iconSize, iconSize));
            reporteBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/reporte.png")), iconSize, iconSize));
        } catch (Exception e) {
            System.err.println("Error al cargar los iconos de los botones: " + e.getMessage());
        }

        // === ACTION LISTENERS SIMPLIFICADOS ===
        // La Vista solo recoge los datos y llama al controlador.

        guardarBtn.addActionListener(e -> {
            Paciente paciente = new Paciente();
            paciente.setId(idFld.getText());
            paciente.setNombre(nombreFld.getText());
            paciente.setTelefono(telefonoFld.getText());
            paciente.setFechaNacimiento(fechaNacimientoFld.getDate());
            controller.guardarPaciente(paciente);
        });

        borrarBtn.addActionListener(e -> {
            int filaSeleccionada = pacientesTbl.getSelectedRow();
            if (filaSeleccionada != -1) {
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "¿Está seguro de que desea eliminar a este paciente?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.borrarPaciente(filaSeleccionada);
                }
            }
        });

        buscarBtn.addActionListener(e -> controller.buscarPaciente(buscarFld.getText()));
        limpiarBtn.addActionListener(e -> controller.clear());
        reporteBtn.addActionListener(e -> controller.generarReporte());

        // --- Listener para la selección de la tabla (sin cambios) ---
        pacientesTbl.getSelectionModel().addListSelectionListener((ListSelectionEvent ev) -> {
            if (!ev.getValueIsAdjusting()) {
                int selectedRow = pacientesTbl.getSelectedRow();
                if (selectedRow >= 0) {
                    controller.edit(selectedRow);
                }
            }
        });

        // --- CÓDIGO PARA CREAR Y AÑADIR EL JDateChooser (sin cambios) ---
        fechaNacimientoFld = new com.toedter.calendar.JDateChooser();
        dateChooserPanel.setLayout(new BorderLayout());
        dateChooserPanel.add(fechaNacimientoFld, BorderLayout.CENTER);
    }

    public void setController(Controller controller) { this.controller = controller; }
    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        this.pacientesTableModel = new TableModel(model.getList());
        this.pacientesTbl.setModel(pacientesTableModel);
    }

    // === PROPERTYCHANGE AHORA MANEJA TODAS LAS ACTUALIZACIONES ===
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        // 1. Escucha por errores y los muestra
        if (evt.getPropertyName().equals("errorMessage")) {
            String error = (String) evt.getNewValue();
            if (error != null && !error.isEmpty()) {
                JOptionPane.showMessageDialog(panel, error, "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // 2. Escucha por cambios en la lista y actualiza la tabla
        if (evt.getPropertyName().equals(Model.LIST)) {
            SwingUtilities.invokeLater(() -> {
                pacientesTableModel.setRows(model.getList());
                panel.revalidate();
            });
        }

        // 3. Escucha por cambios en el elemento actual y actualiza el formulario
        if (evt.getPropertyName().equals(Model.CURRENT)) {
            Paciente p = model.getCurrent();
            idFld.setText(p.getId());
            nombreFld.setText(p.getNombre());
            telefonoFld.setText(p.getTelefono());
            fechaNacimientoFld.setDate(p.getFechaNacimiento());

            boolean isNew = p.getId().isEmpty();
            borrarBtn.setEnabled(!isNew);
            idFld.setEditable(isNew);
        }
    }

    public JPanel getPanel() { return panel; }
}