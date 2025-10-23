package hospital.frontend.presentation.mainview.medicos;

import hospital.protocol.logic.Medico;
import hospital.frontend.logic.Service;
import javax.swing.SwingWorker; // Importamos SwingWorker

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setModel(model);
        view.setController(this);
        this.buscar(""); // Carga inicial de datos
    }

    // --- MÉTODO 'GUARDAR' CORREGIDO CON TRY-CATCH Y SWINGWORKER ---
    public void guardar(Medico medicoFromForm) {
        try {
            String id = medicoFromForm.getId().trim();
            String nombre = medicoFromForm.getNombre().trim();
            String especialidad = medicoFromForm.getEspecialidad().trim();

            if (id.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
                throw new Exception("La cédula, el nombre y la especialidad son requeridos.");
            }

            // Usamos SwingWorker para las operaciones de red
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Medico medicoAGuardar = new Medico();
                    medicoAGuardar.setId(id);
                    medicoAGuardar.setNombre(nombre);
                    medicoAGuardar.setEspecialidad(especialidad);
                    medicoAGuardar.setTipo("Medico");

                    boolean esNuevo = model.getCurrent().getId().isEmpty();
                    if (esNuevo) {
                        medicoAGuardar.setClave(id); // Clave por defecto es el ID
                        Service.getInstance().createMedico(medicoAGuardar);
                    } else {
                        medicoAGuardar.setClave(model.getCurrent().getClave());
                        Service.getInstance().updateMedico(medicoAGuardar);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Captura cualquier error del hilo de fondo
                        buscar(""); // Refresca la lista
                        limpiar();  // Limpia el formulario
                    } catch (Exception ex) {
                        model.setErrorMessage(ex.getCause().getMessage());
                    }
                }
            }.execute();

        } catch (Exception e) {
            // Error de validación local
            model.setErrorMessage(e.getMessage());
        }
    }

    // --- MÉTODO 'BORRAR' CORREGIDO CON TRY-CATCH Y SWINGWORKER ---
    public void borrar(Medico medico) {
        try {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Service.getInstance().deleteMedico(medico.getId());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        buscar("");
                        limpiar();
                    } catch (Exception ex) {
                        model.setErrorMessage(ex.getCause().getMessage());
                    }
                }
            }.execute();
        } catch (Exception e) {
            model.setErrorMessage("No se pudo iniciar la operación de borrado.");
        }
    }

    // --- MÉTODO 'BUSCAR' CORREGIDO ---
    public void buscar(String filtro) {
        try {
            model.setList(Service.getInstance().searchMedicos(filtro.trim()));
        } catch (Exception e) {
            // Informamos al usuario si la búsqueda falla.
            model.setErrorMessage(e.getMessage());
        }
    }

    public void editar(int row) {
        model.setCurrent(model.getList().get(row));
    }

    public void limpiar() {
        model.setCurrent(new Medico());
    }
}