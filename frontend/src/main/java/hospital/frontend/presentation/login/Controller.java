package hospital.frontend.presentation.login;

import hospital.frontend.logic.Service;
import hospital.protocol.logic.Usuario;
import hospital.frontend.logic.Sesion;
import javax.swing.JDialog;

import hospital.frontend.application.Application;


public class Controller {
    private View view;
    private Model model;
    private JDialog dialog;

    public Controller(View view, Model model, JDialog dialog) {
        this.dialog = dialog;
        this.view = view;
        this.model = model;
        view.setModel(model);
        view.setController(this);
    }

    // --- MÉTODO INGRESAR CORREGIDO ---
    public void ingresar(String id, String clave) {
        try {
            // Usamos los parámetros 'id' y 'clave' que recibe el método
            Usuario validado = Service.getInstance().autenticar(id, clave);
            Sesion.setUsuario(validado);
            dialog.dispose();
        } catch (Exception e) {
            view.mostrarError(e.getMessage());
        }
    }
//Metodo salir
    public void salir() {
        System.exit(0);
    }

    public void cambiarClave() {
        try {
            JDialog dialog = new JDialog(Application.getWindow(), "Cambiar Contraseña", true);

            // Creamos el MVC del módulo para cambiar clave
            hospital.frontend.presentation.chpass.View chpassView = new hospital.frontend.presentation.chpass.View();
            new hospital.frontend.presentation.chpass.Controller(chpassView, dialog);

            // Configuramos y mostramos el diálogo
            dialog.setContentPane(chpassView.getPanel());
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null); // Centrado en la pantalla
            dialog.setVisible(true);
        } catch (Exception e) {
            // Manejo de error por si el diálogo no se puede crear
            System.err.println("Error al abrir el diálogo de cambio de clave: " + e.getMessage());
        }
    }
}
