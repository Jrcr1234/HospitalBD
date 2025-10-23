package hospital.frontend.presentation.mainview.medicamentos;

import hospital.protocol.logic.Medicamento;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import hospital.frontend.presentation.util.GuiUtils;

public class View implements PropertyChangeListener {
    private JPanel panel;
    private JTable medicamentosTbl;
    private JPanel formularioPanel;
    private JPanel busquedaPanel;
    private JPanel tablaPanel;
    private JTextField codigoFld;
    private JTextField nombreFld;
    private JTextField presentacionFld;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;
    private JButton buscarBtn;
    private JTextField buscarFld;

    private Controller controller;
    private Model model;
    private TableModel medicamentosTableModel;

    public View() {
        // El constructor AHORA ESTÁ VACÍO.
    }


    public void init() {
        // === CÓDIGO PARA AÑADIR ICONOS A LOS BOTONES ===
        try {
            int iconSize = 24;


            ImageIcon guardarIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/guardar.png")), iconSize, iconSize);
            guardarBtn.setIcon(guardarIcon);

            ImageIcon borrarIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/Cancelar.png")), iconSize, iconSize);
            borrarBtn.setIcon(borrarIcon);

            ImageIcon limpiarIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/limpiar.png")), iconSize, iconSize);
            limpiarBtn.setIcon(limpiarIcon);

            ImageIcon buscarIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/buscar.png")), iconSize, iconSize);
            buscarBtn.setIcon(buscarIcon);

        } catch (Exception e) {
            System.err.println("Error al cargar los iconos de los botones: " + e.getMessage());
        }
// =======================================================


        // --- ActionListeners para los botones ---
        guardarBtn.addActionListener(e -> {
            Medicamento med = new Medicamento();
            med.setCodigo(codigoFld.getText());
            med.setNombre(nombreFld.getText());
            med.setPresentacion(presentacionFld.getText());
            controller.save(med); // Llama al controller para que decida si guardar o modificar
        });

        borrarBtn.addActionListener(e -> {
            int selectedRow = medicamentosTbl.getSelectedRow();
            if (selectedRow >= 0) {
                controller.delete(selectedRow);
            }
        });

        buscarBtn.addActionListener(e -> controller.search(buscarFld.getText()));
        limpiarBtn.addActionListener(e -> controller.clear());

        // --- Listener para la selección de la tabla ---
        medicamentosTbl.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = medicamentosTbl.getSelectedRow();
                if (selectedRow >= 0) {
                    controller.edit(selectedRow);
                }
            }
        });
    }

    public void setController(Controller controller) { this.controller = controller; }
    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);

        this.medicamentosTableModel = new TableModel(model.getList());
        this.medicamentosTbl.setModel(medicamentosTableModel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // --- Lógica para actualizar el formulario  ---

        if (evt.getPropertyName().equals("errorMessage")) {
            String error = (String) evt.getNewValue();
            if (error != null && !error.isEmpty()) {
                JOptionPane.showMessageDialog(panel, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (evt.getPropertyName().equals(Model.CURRENT)) {
            codigoFld.setText(model.getCurrent().getCodigo());
            nombreFld.setText(model.getCurrent().getNombre());
            presentacionFld.setText(model.getCurrent().getPresentacion());

            boolean isNew = model.getCurrent().getCodigo().isEmpty();
            guardarBtn.setEnabled(true);
            borrarBtn.setEnabled(!isNew);
            codigoFld.setEditable(isNew);
        }

        // --- Lógica para actualizar la TABLA (esta es la parte que mejoramos) ---
        if (evt.getPropertyName().equals(Model.LIST)) {
            // Usamos invokeLater para asegurar que la actualización visual se procese correctamente
            SwingUtilities.invokeLater(() -> {
                // Le decimos a nuestro TableModel existente que actualice sus datos
                medicamentosTableModel.setRows(model.getList());
                // Forzamos al panel a redibujarse
                panel.revalidate();
                panel.repaint();
            });
        }
    }

    public JPanel getPanel() { return panel; }
}
