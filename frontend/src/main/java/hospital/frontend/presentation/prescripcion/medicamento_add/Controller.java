package hospital.frontend.presentation.prescripcion.medicamento_add;

import hospital.frontend.application.Application;
import hospital.protocol.logic.LineaDetalle;
import hospital.protocol.logic.Medicamento;
import hospital.frontend.logic.Service;
import javax.swing.JDialog;
import java.util.List;

public class Controller {
    private View view;
    private Model model;
    private JDialog dialog;
    private hospital.frontend.presentation.prescripcion.Model prescripcionModel; // El modelo de la ventana principal

    public Controller(View view, Model model, JDialog dialog, hospital.frontend.presentation.prescripcion.Model prescripcionModel) {
        this.view = view;
        this.model = model;
        this.dialog = dialog;
        this.prescripcionModel = prescripcionModel;
        view.setModel(model);
        view.setController(this);

        // Carga inicial de todos los medicamentos, ahora con manejo de errores.
        try {
            model.setList(Service.getInstance().getMedicamentos());
        } catch (Exception e) {
            model.setErrorMessage("Error al cargar la lista de medicamentos: " + e.getMessage());
        }
    }

    // --- MÉTODO 'BUSCAR' CORREGIDO ---
    public void buscar() {
        try {
            String termino = view.getTerminoBusqueda();
            // La llamada al servicio ahora está protegida.
            List<Medicamento> resultado = Service.getInstance().searchMedicamentos(termino);
            model.setList(resultado);
        } catch (Exception e) {
            // Si la búsqueda falla, se lo notificamos al modelo local.
            model.setErrorMessage(e.getMessage());
        }
    }

    // --- MÉTODO 'SELECCIONAR' CORREGIDO ---
    // Orquesta la apertura del siguiente diálogo.
    public void seleccionar() {
        try {
            Medicamento seleccionado = view.getSelected();
            if (seleccionado == null) {
                throw new Exception("Debe seleccionar un medicamento de la lista.");
            }

            // 1. Cerramos el diálogo de búsqueda actual.
            this.dialog.dispose();

            // 2. Abrimos el diálogo de detalles de dosis.
            JDialog detailsDialog = new JDialog(Application.getWindow(), "Detalles de Dosis", true);

            // Creamos el MVC para el diálogo de detalles.
            hospital.frontend.presentation.prescripcion.dosis_details.Model detailsModel = new hospital.frontend.presentation.prescripcion.dosis_details.Model(seleccionado);
            hospital.frontend.presentation.prescripcion.dosis_details.View detailsView = new hospital.frontend.presentation.prescripcion.dosis_details.View();
            new hospital.frontend.presentation.prescripcion.dosis_details.Controller(detailsView, detailsModel, detailsDialog, this.prescripcionModel);

            detailsView.init(); // Inicializamos la vista de detalles

            detailsDialog.setContentPane(detailsView.getPanel());
            detailsDialog.pack();
            detailsDialog.setResizable(false);
            detailsDialog.setLocationRelativeTo(Application.getWindow());
            detailsDialog.setVisible(true);

        } catch (Exception e) {
            // Si hay un error (ej. no se seleccionó medicamento), lo notificamos.
            model.setErrorMessage(e.getMessage());
        }
    }

    public void cancelar() {
        dialog.dispose();
    }
}