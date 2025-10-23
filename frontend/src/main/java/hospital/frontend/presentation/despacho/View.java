package hospital.frontend.presentation.despacho;

import hospital.frontend.presentation.util.GuiUtils;
import hospital.protocol.logic.EstadoReceta;
import hospital.protocol.logic.Receta;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.*;

public class View implements PropertyChangeListener {
    private JPanel panel;
    private JTextField searchFld;
    private JButton searchBtn;
    private JScrollPane scrollPane;
    private JTable recetasTbl;
    private JPanel buscarPanel;
    private JPanel tablaPanel;
    private JPanel procesoPanel;
    private JButton procesoBtn;
    private JButton listaBtn;
    private JButton entregarBtn;

    private Controller controller;
    private Model model;
    private TableModel tableModel;

    public View() {}

    public void init() {
        // --- ActionListeners Simplificados ---
        searchBtn.addActionListener(e -> controller.buscar());
        procesoBtn.addActionListener(e -> controller.cambiarEstado(EstadoReceta.PROCESO));
        listaBtn.addActionListener(e -> controller.cambiarEstado(EstadoReceta.LISTA));
        entregarBtn.addActionListener(e -> controller.cambiarEstado(EstadoReceta.ENTREGADA));

        // --- Asignación de Iconos ---
        try {
            int iconSize = 24;
            searchBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/buscar.png")), iconSize, iconSize));
        } catch (Exception e) {
            System.err.println("Error al cargar iconos de despacho: " + e.getMessage());
        }
    }

    public JPanel getPanel() { return panel; }
    public String getSearchText() { return searchFld.getText(); }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this); // La vista se suscribe a los cambios
        tableModel = new TableModel(model.getRecetas());
        recetasTbl.setModel(tableModel);
    }

    // Método para que el controlador obtenga la receta seleccionada
    public Receta getSelected() {
        int selectedRow = recetasTbl.getSelectedRow();
        if (selectedRow < 0) return null;
        return model.getRecetas().get(selectedRow);
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

        // 2. Escucha por cambios en la lista de recetas y actualiza la tabla
        if (evt.getPropertyName().equals(Model.RECETAS)) {
            SwingUtilities.invokeLater(() -> {
                tableModel.setRows(model.getRecetas());
                panel.revalidate();
            });
        }
    }
}