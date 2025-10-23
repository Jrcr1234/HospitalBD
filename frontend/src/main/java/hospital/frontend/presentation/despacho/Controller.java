package hospital.frontend.presentation.despacho;

import hospital.protocol.logic.EstadoReceta;
import hospital.protocol.logic.Receta;
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
    }

    // El método 'show' ahora maneja la carga inicial.
    public void show() {
        this.view.getPanel().setVisible(true);
        buscar(); // Carga inicial de datos
    }

    // --- MÉTODO 'BUSCAR' CORREGIDO ---
    public void buscar() {
        try {
            String filtro = view.getSearchText();
            // La llamada al servicio ahora está protegida.
            model.setRecetas(Service.getInstance().searchRecetasDespacho(filtro));
        } catch (Exception e) {
            // Si la búsqueda falla, se lo notificamos al modelo.
            model.setErrorMessage(e.getMessage());
        }
    }

    // --- MÉTODO 'CAMBIARESTADO' CORREGIDO CON MVC Y SWINGWORKER ---
    public void cambiarEstado(EstadoReceta nuevoEstado) {
        try {
            Receta seleccionada = view.getSelected();
            if (seleccionada == null) {
                // La validación ahora pasa por el modelo.
                throw new Exception("Debe seleccionar una receta.");
            }

            // Usamos SwingWorker para la operación de red, evitando congelar la UI.
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // La llamada al servicio se hace en un hilo de fondo.
                    Service.getInstance().updateRecetaEstado(seleccionada.getCodigo(), nuevoEstado);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Captura cualquier error del hilo de fondo.
                        buscar(); // Si todo salió bien, refresca la tabla.
                    } catch (Exception ex) {
                        // Si el servicio falló, pasamos el error al modelo.
                        model.setErrorMessage(ex.getCause().getMessage());
                    }
                }
            }.execute();

        } catch (Exception e) {
            // Captura el error de validación (si no se seleccionó receta).
            model.setErrorMessage(e.getMessage());
        }
    }
}