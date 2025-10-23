package hospital.frontend.presentation.chpass;

import hospital.frontend.logic.Service;
import hospital.protocol.logic.Usuario;
import javax.swing.JDialog;

public class Controller {
    private View view;
    private JDialog dialog;

    public Controller(View view, JDialog dialog) {
        this.view = view;
        this.dialog = dialog;
        view.setController(this);
    }

    // --- MÉTODO 'cambiarClave' CORREGIDO ---
    // Ya no usa 'throws Exception'
    public void cambiarClave() {
        try {
            String id = view.getId().trim();
            String actual = view.getClaveActual().trim();
            String nueva = view.getClaveNueva().trim();
            String confirmar = view.getConfirmarClave().trim();

            // 1. Validaciones locales
            if (id.isEmpty()) throw new Exception("Debe ingresar su cédula.");
            if (nueva.isEmpty()) throw new Exception("La clave nueva no puede estar vacía.");
            if (!nueva.equals(confirmar)) throw new Exception("La clave nueva y su confirmación no coinciden.");

            // 2. Llamada al servicio (puede fallar por red o clave incorrecta)
            Service.getInstance().cambiarClave(id, actual, nueva);

            // 3. Si todo sale bien, le pedimos a la vista que muestre éxito
            view.showSuccess();
            this.cancelar();

        } catch (Exception e) {
            // 4. Si algo falla, le pedimos a la vista que muestre el error
            view.showError(e.getMessage());
        }
    }

    public void cancelar() {
        dialog.dispose();
    }
}