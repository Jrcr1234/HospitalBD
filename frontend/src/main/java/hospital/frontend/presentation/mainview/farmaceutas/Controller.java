package hospital.frontend.presentation.mainview.farmaceutas;

import hospital.protocol.logic.Farmaceuta;
import hospital.frontend.logic.Service;
import javax.swing.SwingWorker; // Importamos SwingWorker para las tareas de red

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setModel(model);
        view.setController(this);
        // Carga inicial de datos
        this.buscar("");
    }

    // --- MÉTODO 'GUARDAR' CORREGIDO CON TRY-CATCH Y SWINGWORKER ---
    public void guardar(Farmaceuta farmaceutaFromForm) {
        try {
            String id = farmaceutaFromForm.getId().trim();
            String nombre = farmaceutaFromForm.getNombre().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                throw new Exception("La cédula y el nombre son requeridos.");
            }

            // Usamos SwingWorker para no bloquear la interfaz durante la operación de red
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Farmaceuta farmaceutaAGuardar = new Farmaceuta();
                    farmaceutaAGuardar.setId(id);
                    farmaceutaAGuardar.setNombre(nombre);
                    farmaceutaAGuardar.setTipo("Farmaceuta");

                    boolean esNuevo = model.getCurrent().getId().isEmpty();
                    if (esNuevo) {
                        farmaceutaAGuardar.setClave(id); // La clave por defecto es el ID
                        Service.getInstance().createFarmaceuta(farmaceutaAGuardar);
                    } else {
                        farmaceutaAGuardar.setClave(model.getCurrent().getClave());
                        Service.getInstance().updateFarmaceuta(farmaceutaAGuardar);
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
    public void borrar(Farmaceuta farmaceuta) {
        try {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Service.getInstance().deleteFarmaceuta(farmaceuta.getId());
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
            model.setList(Service.getInstance().searchFarmaceutas(filtro.trim()));
        } catch (Exception e) {
            // Ahora sí le informamos al usuario si la búsqueda falla.
            model.setErrorMessage(e.getMessage());
        }
    }

    public void editar(int row) {
        model.setCurrent(model.getList().get(row));
    }

    public void limpiar() {
        model.setCurrent(new Farmaceuta());
    }
}