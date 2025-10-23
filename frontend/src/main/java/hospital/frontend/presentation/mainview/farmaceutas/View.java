package hospital.frontend.presentation.mainview.farmaceutas;

import hospital.protocol.logic.Farmaceuta;
import hospital.frontend.presentation.util.GuiUtils;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {
    // --- Atributos de la Interfaz  ---
    private JPanel panel;
    private JTable farmaceutasTbl;
    private JTextField idFld;
    private JTextField nombreFld;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;
    private JButton buscarBtn;
    private JTextField buscarFld;
    private JPanel formularioPanel;
    private JPanel busquedaPanel;
    private JPanel tablaPanel;

    // --- Variables para el MVC  ---
    private Controller controller;
    private Model model;
    private TableModel farmaceutasTableModel;

    public View() {
        // Constructor vacío
    }

    public void init() {
        // === LOS ACTION LISTENERS AHORA SON MUCHO MÁS SIMPLES ===
        // Su única responsabilidad es llamar al controlador.

        guardarBtn.addActionListener(e -> {
            // La Vista solo recoge los datos y los envía al controlador.
            // No valida, no muestra mensajes, no maneja errores.
            Farmaceuta farmaceutaFromForm = new Farmaceuta();
            farmaceutaFromForm.setId(idFld.getText());
            farmaceutaFromForm.setNombre(nombreFld.getText());
            controller.guardar(farmaceutaFromForm);
        });

        borrarBtn.addActionListener(e -> {
            int selectedRow = farmaceutasTbl.getSelectedRow();
            if (selectedRow != -1) {
                // Pide confirmación ANTES de llamar al controlador.
                // Esto es aceptable en la vista, ya que es parte de la interacción del usuario.
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "¿Está seguro de que desea eliminar a este farmaceuta?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Farmaceuta farmaceutaParaBorrar = model.getList().get(selectedRow);
                    controller.borrar(farmaceutaParaBorrar);
                }
            }
        });

        limpiarBtn.addActionListener(e -> controller.limpiar());
        buscarBtn.addActionListener(e -> controller.buscar(buscarFld.getText()));

        // --- Listener para la selección de la tabla (sin cambios) ---
        farmaceutasTbl.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = farmaceutasTbl.getSelectedRow();
                if (selectedRow >= 0) controller.editar(selectedRow);
            }
        });

        // --- Código para los iconos ---
        try {
            int iconSize = 24;
            guardarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/guardar.png")), iconSize, iconSize));
            borrarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/Cancelar.png")), iconSize, iconSize));
            limpiarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/limpiar.png")), iconSize, iconSize));
            buscarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/buscar.png")), iconSize, iconSize));
        } catch (Exception e) { System.err.println("Error al cargar iconos de farmaceutas: " + e.getMessage()); }
    }

    public JPanel getPanel() { return panel; }
    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this); // La vista se suscribe a los cambios del modelo
        farmaceutasTableModel = new TableModel(model.getList());
        farmaceutasTbl.setModel(farmaceutasTableModel);
    }

    // === EL MÉTODO PROPERTYCHANGE AHORA CENTRALIZA TODAS LAS ACTUALIZACIONES DE LA VISTA ===
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
                farmaceutasTableModel.setRows(model.getList());
                panel.revalidate();
            });
        }

        // 3. Escucha por cambios en el elemento actual y actualiza el formulario
        if (evt.getPropertyName().equals(Model.CURRENT)) {
            Farmaceuta f = model.getCurrent();
            idFld.setText(f.getId());
            nombreFld.setText(f.getNombre());

            boolean isNew = f.getId().isEmpty();
            borrarBtn.setEnabled(!isNew);
            idFld.setEditable(isNew);
        }
    }
}