package hospital.frontend.presentation.prescripcion.dosis_details;

import hospital.protocol.logic.LineaDetalle;
import javax.swing.JDialog;
import java.util.ArrayList;

public class Controller {
    private Model model;
    private View view;
    private JDialog dialog;
    private hospital.frontend.presentation.prescripcion.Model prescripcionModel;

    public Controller(View view, Model model, JDialog dialog, hospital.frontend.presentation.prescripcion.Model prescripcionModel) {
        this.view = view;
        this.model = model;
        this.dialog = dialog;
        this.prescripcionModel = prescripcionModel;
        view.setModel(model);
        view.setController(this);
    }

    // --- MÉTODO 'GUARDAR' CORREGIDO CON ARQUITECTURA MVC ---
    public void guardar() {
        try {
            // 1. Obtenemos y validamos los datos de la vista.
            // Estos getters en la vista deberían manejar la conversión de texto a número
            // y lanzar una excepción si el formato es incorrecto.
            int cantidad = view.getCantidad();
            String indicaciones = view.getIndicaciones();
            int duracion = view.getDuracion();

            // 2. Aplicamos los cambios al modelo principal de prescripción.
            if (model.getLineaExistente() != null) {
                // MODO EDICIÓN: Actualizamos el objeto existente.
                LineaDetalle linea = model.getLineaExistente();
                linea.setCantidad(cantidad);
                linea.setIndicaciones(indicaciones);
                linea.setDuracionTratamiento(duracion);

                // Forzamos la notificación para que la tabla principal se actualice.
                prescripcionModel.setLineas(new ArrayList<>(prescripcionModel.getLineas()));
            } else {
                // MODO AGREGAR: (Esta lógica pertenece a 'medicamento_add', pero la dejamos por si se reutiliza)
                LineaDetalle nuevaLinea = new LineaDetalle();
                nuevaLinea.setMedicamento(model.getSeleccionado());
                nuevaLinea.setCantidad(cantidad);
                nuevaLinea.setIndicaciones(indicaciones);
                nuevaLinea.setDuracionTratamiento(duracion);

                // Usamos el método que creamos en el modelo principal.
                prescripcionModel.addLinea(nuevaLinea);
            }

            // 3. Si todo sale bien, cerramos el diálogo.
            this.cancelar();

        } catch (Exception e) {
            // 4. Si hay un error de validación, se lo pasamos al MODELO LOCAL.
            // La vista de este diálogo se encargará de mostrar el error.
            model.setErrorMessage(e.getMessage());
        }
    }

    public void cancelar() {
        dialog.dispose();
    }
}