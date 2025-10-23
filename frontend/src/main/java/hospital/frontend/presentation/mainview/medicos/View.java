package hospital.frontend.presentation.mainview.medicos;

import hospital.frontend.presentation.util.GuiUtils;
import hospital.protocol.logic.Medico;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ListSelectionEvent;

public class View implements PropertyChangeListener {
    // --- Atributos de la Interfaz  ---
    private JPanel panel;
    private JPanel formularioPanel;
    private JPanel busquedaPanel;
    private JPanel tablaPanel;
    private JTextField idFld;
    private JTextField especialidadFld;
    private JTextField nombreFld;
    private JButton limpiarBtn;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JTextField buscarFld;
    private JButton reporteBtn;
    private JButton buscarBtn;
    private JTable medicosTbl;

    // --- Variables para el MVC  ---
    private Controller controller;
    private Model model;
    private TableModel medicosTableModel;

    public View() {
        // Constructor vacío
    }

    public void init() {
        // --- Asignación de Iconos  ---
        try {
            int iconSize = 24;
            guardarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/guardar.png")), iconSize, iconSize));
            borrarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/Cancelar.png")), iconSize, iconSize));
            limpiarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/limpiar.png")), iconSize, iconSize));
            buscarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/buscar.png")), iconSize, iconSize));
            reporteBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/reporte.png")), iconSize, iconSize));
        } catch (Exception e) {
            System.err.println("Error al cargar los iconos: " + e.getMessage());
        }

        // === ACTION LISTENERS SIMPLIFICADOS ===
        // La vista solo recoge datos y llama al controlador.

        guardarBtn.addActionListener(e -> {
            Medico medicoFromForm = new Medico();
            medicoFromForm.setId(idFld.getText());
            medicoFromForm.setNombre(nombreFld.getText());
            medicoFromForm.setEspecialidad(especialidadFld.getText());
            controller.guardar(medicoFromForm);
        });

        borrarBtn.addActionListener(e -> {
            int selectedRow = medicosTbl.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "¿Está seguro de que desea eliminar a este médico?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Medico medicoParaBorrar = model.getList().get(selectedRow);
                    controller.borrar(medicoParaBorrar);
                }
            }
        });

        limpiarBtn.addActionListener(e -> controller.limpiar());
        buscarBtn.addActionListener(e -> controller.buscar(buscarFld.getText()));
        reporteBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Funcionalidad de reporte aún no implementada."));

        // --- Listener de selección de tabla  ---
        medicosTbl.getSelectionModel().addListSelectionListener((ListSelectionEvent ev) -> {
            if (!ev.getValueIsAdjusting()) {
                int selectedRow = medicosTbl.getSelectedRow();
                if (selectedRow >= 0) {
                    controller.editar(selectedRow);
                }
            }
        });
    }

    public JPanel getPanel() { return panel; }
    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this); // La vista se suscribe a los cambios
        medicosTableModel = new TableModel(model.getList());
        medicosTbl.setModel(medicosTableModel);
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
        if (evt.getPropertyName().equals(Model.LIST)) {
            SwingUtilities.invokeLater(() -> {
                medicosTableModel.setRows(model.getList());
                panel.revalidate();
            });
        }

        // 3. Escucha por cambios en el elemento actual y actualiza el formulario
        if (evt.getPropertyName().equals(Model.CURRENT)) {
            Medico medico = model.getCurrent();
            idFld.setText(medico.getId());
            nombreFld.setText(medico.getNombre());
            especialidadFld.setText(medico.getEspecialidad());

            boolean isNew = medico.getId().isEmpty();
            borrarBtn.setEnabled(!isNew);
            idFld.setEditable(isNew);
        }
    }
}