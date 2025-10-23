package hospital.frontend.presentation.prescripcion.paciente_search;

import hospital.protocol.logic.Paciente;
import hospital.frontend.logic.Service;
import javax.swing.JDialog;
import java.util.List; // Importamos List

public class Controller {
    private View view;
    private Model model;
    private JDialog dialog;
    private hospital.frontend.presentation.prescripcion.Model prescripcionModel;

    public Controller(View view, Model model, JDialog dialog, hospital.frontend.presentation.prescripcion.Model prescripcionModel) {
        this.view = view;
        this.model = model;
        this.dialog = dialog;
        this.prescripcionModel = prescripcionModel;
        view.setModel(model);
        view.setController(this);

        // Hacemos una búsqueda inicial para mostrar todos los pacientes al abrir.
        this.buscar();
    }

    // --- MÉTODO 'BUSCAR' CORREGIDO CON MANEJO DE ERRORES ---
    public void buscar() {
        try {
            String criterio = view.getTerminoBusqueda();
            // La llamada al servicio ahora está protegida.
            List<Paciente> pacientes = Service.getInstance().searchPacientes(criterio);
            model.setList(pacientes);
        } catch (Exception e) {
            // Si la búsqueda falla (ej. error de red), lo notificamos al modelo local.
            model.setErrorMessage(e.getMessage());
        }
    }

    // --- MÉTODO 'SELECCIONAR' CORREGIDO CON VALIDACIÓN ---
    public void seleccionar() {
        try {
            Paciente seleccionado = view.getSelectedPatient();
            if (seleccionado == null) {
                throw new Exception("Debe seleccionar un paciente de la lista.");
            }
            // Actualizamos el modelo de la pantalla principal de prescripción.
            prescripcionModel.setPaciente(seleccionado);
            this.cancelar(); // Cerramos el diálogo.
        } catch (Exception e) {
            // Si hay un error (ej. no se seleccionó paciente), lo notificamos.
            model.setErrorMessage(e.getMessage());
        }
    }

    public void cancelar() {
        dialog.dispose();
    }
}