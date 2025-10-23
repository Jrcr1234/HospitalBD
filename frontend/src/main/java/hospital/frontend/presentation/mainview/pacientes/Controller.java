package hospital.frontend.presentation.mainview.pacientes;

import hospital.protocol.logic.Paciente;
import hospital.frontend.logic.Service;
import java.util.List;
import javax.swing.SwingWorker;

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setModel(model);
        view.setController(this);
        // Carga inicial de datos, ahora con manejo de errores.
        this.buscarPaciente("");
    }

    // --- MÉTODO 'guardarPaciente' CORREGIDO ---
    public void guardarPaciente(Paciente paciente) {
        try {
            if (paciente.getId().isEmpty() || paciente.getNombre().isEmpty()) {
                throw new Exception("La cédula y el nombre son requeridos.");
            }

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // La lógica para decidir si es crear o actualizar se hace en el hilo de fondo.
                    if (model.getCurrent().getId().isEmpty()) {
                        Service.getInstance().createPaciente(paciente);
                    } else {
                        Service.getInstance().updatePaciente(paciente);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Captura cualquier error de la operación de red.
                        clear();
                        buscarPaciente(""); // Refresca la vista.
                    } catch (Exception ex) {
                        // Se le pasa el error al modelo para que la vista lo muestre.
                        model.setErrorMessage(ex.getCause().getMessage());
                    }
                }
            }.execute();

        } catch (Exception e) {
            // Error de validación local, se le pasa al modelo.
            model.setErrorMessage(e.getMessage());
        }
    }

    // --- MÉTODO 'borrarPaciente' CORREGIDO ---
    public void borrarPaciente(int rowIndex) {
        try {
            Paciente pacienteABorrar = model.getList().get(rowIndex);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Service.getInstance().deletePaciente(pacienteABorrar.getId());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        buscarPaciente(""); // Refresca la tabla
                    } catch (Exception e) {
                        model.setErrorMessage(e.getCause().getMessage());
                    }
                }
            }.execute();

        } catch (Exception e) {
            model.setErrorMessage("No se pudo seleccionar el paciente a borrar.");
        }
    }

    // --- MÉTODO 'buscarPaciente' CORREGIDO ---
    public void buscarPaciente(String filtro) {
        try {
            List<Paciente> rows = Service.getInstance().searchPacientes(filtro);
            model.setList(rows);
        } catch (Exception e) {
            // Si la búsqueda falla, se lo informamos al usuario.
            model.setErrorMessage(e.getMessage());
        }
    }

    public void generarReporte() {
        // La vista se encargará de mostrar este mensaje.
        model.setErrorMessage("Funcionalidad de reporte aún no implementada.");
    }

    public void clear() {
        model.setCurrent(new Paciente());
    }

    public void edit(int row) {
        Paciente p = model.getList().get(row);
        model.setCurrent(p);
    }
}