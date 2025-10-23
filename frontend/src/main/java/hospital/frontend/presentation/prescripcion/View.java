package hospital.frontend.presentation.prescripcion;

import hospital.frontend.application.Application;
import hospital.protocol.logic.LineaDetalle;
import hospital.frontend.presentation.util.GuiUtils;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

public class View implements PropertyChangeListener {
    // --- Atributos de la Interfaz (sin cambios) ---
    private JPanel panel;
    private JPanel controlPanel;
    private JButton buscarPacienteBtn;
    private JButton agregarMedicamentoBtn;
    private JLabel pacienteLbl;
    private JTable medicamentosTbl;
    private JButton guardarRecetaBtn;
    private JButton limpiarBtn;
    private JButton descartarMedBtn;
    private JButton detallesBtn;
    private JPanel dateChooserPanelPrescripcion;
    private JPanel fechaConfeccionPanel;
    private JPanel recetaPanel;
    private JPanel infoPanel;
    private JPanel ajustePanel;
    private com.toedter.calendar.JDateChooser fechaRetiroFld;
    private com.toedter.calendar.JDateChooser fechaConfeccionFld;

    // --- Variables MVC ---
    private Controller controller;
    private Model model;
    private TableModel tableModel;

    public View() {}

    public void init() {
        // --- Asignación de Iconos ---
        try {
            int iconSize = 24;
            buscarPacienteBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/buscarPaciente.png")), iconSize, iconSize));
            agregarMedicamentoBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/agregar.png")), iconSize, iconSize));
            limpiarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/limpiar.png")), iconSize, iconSize));
            guardarRecetaBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/guardar.png")), iconSize, iconSize));
            descartarMedBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/descartar.png")), iconSize, iconSize));
            detallesBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/detalles.png")), iconSize, iconSize));
        } catch (Exception e) {
            System.err.println("Error al cargar iconos de prescripción: " + e.getMessage());
        }

        // === ACTION LISTENERS SIMPLIFICADOS ===
        // La Vista ya no abre diálogos ni maneja errores, solo llama al Controller.
        buscarPacienteBtn.addActionListener(e -> controller.buscarPaciente());
        agregarMedicamentoBtn.addActionListener(e -> controller.agregarMedicamento());
        guardarRecetaBtn.addActionListener(e -> controller.registrarReceta());
        limpiarBtn.addActionListener(e -> controller.limpiar());

        descartarMedBtn.addActionListener(e -> {
            int selectedRow = medicamentosTbl.getSelectedRow();
            controller.eliminarMedicamento(selectedRow);
        });

        detallesBtn.addActionListener(e -> {
            int selectedRow = medicamentosTbl.getSelectedRow();
            LineaDetalle linea = controller.getLineaParaModificar(selectedRow);
            if (linea != null) {
                // El controlador es el responsable de abrir el diálogo
                abrirDialogoModificarDosis(linea);
            }
        });

        pacienteLbl.setText("Paciente: (Ninguno seleccionado)");

        // --- Creación de Calendarios ---
        try {
            this.fechaRetiroFld = new com.toedter.calendar.JDateChooser();
            this.dateChooserPanelPrescripcion.setLayout(new BorderLayout());
            this.dateChooserPanelPrescripcion.add(this.fechaRetiroFld, BorderLayout.CENTER);

            this.fechaConfeccionFld = new com.toedter.calendar.JDateChooser(new Date());
            this.fechaConfeccionPanel.setLayout(new BorderLayout());
            this.fechaConfeccionPanel.add(this.fechaConfeccionFld, BorderLayout.CENTER);
        } catch (Exception e) {
            System.err.println("Error al crear JDateChooser: " + e.getMessage());
        }
    }

    // El método para abrir el diálogo ahora es llamado por el Controller
    private void abrirDialogoModificarDosis(LineaDetalle lineaParaModificar) {
        // Esta lógica podría moverse también al Controller si se vuelve más compleja,
        // pero por ahora es aceptable aquí ya que es puramente de presentación.
        JDialog detailsDialog = new JDialog(Application.getWindow(), "Modificar Dosis", true);
        hospital.frontend.presentation.prescripcion.dosis_details.Model detailsModel = new hospital.frontend.presentation.prescripcion.dosis_details.Model(lineaParaModificar);
        hospital.frontend.presentation.prescripcion.dosis_details.View detailsView = new hospital.frontend.presentation.prescripcion.dosis_details.View();
        new hospital.frontend.presentation.prescripcion.dosis_details.Controller(detailsView, detailsModel, detailsDialog, this.model);

        detailsDialog.setContentPane(detailsView.getPanel());
        detailsDialog.pack();
        detailsDialog.setResizable(false);
        detailsDialog.setLocationRelativeTo(this.panel);
        detailsDialog.setVisible(true);
    }

    // --- Métodos de enlace MVC y actualización de UI ---
    public JPanel getPanel() { return panel; }
    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        tableModel = new TableModel(model.getLineas());
        medicamentosTbl.setModel(tableModel);
    }

    public Date getFechaRetiro() { return fechaRetiroFld.getDate(); }
    public Date getFechaConfeccion() { return fechaConfeccionFld.getDate(); }

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

        // 2. Escucha por mensajes de éxito y los muestra
        if (evt.getPropertyName().equals("successMessage")) {
            String success = (String) evt.getNewValue();
            if (success != null && !success.isEmpty()) {
                JOptionPane.showMessageDialog(panel, success, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // 3. Actualiza el paciente seleccionado
        if (evt.getPropertyName().equals(Model.PACIENTE)) {
            if (model.getPaciente() != null) {
                pacienteLbl.setText("Paciente: " + model.getPaciente().getNombre());
            } else {
                pacienteLbl.setText("Paciente: (Ninguno seleccionado)");
            }
        }

        // 4. Actualiza la tabla de medicamentos
        if (evt.getPropertyName().equals(Model.LINEAS)) {
            SwingUtilities.invokeLater(() -> {
                tableModel.setRows(model.getLineas());
                panel.revalidate();
            });
        }
    }
}