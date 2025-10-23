package hospital.frontend.presentation.chpass;

import javax.swing.*;

public class View {
    private JPanel panel;
    private JTextField idFld;
    private JPasswordField claveActualFld;
    private JPasswordField claveNuevaFld;
    private JPasswordField confirmarClaveFld;
    private JButton guardarBtn;
    private JButton cancelarBtn;

    private Controller controller;

    public View() {
        // --- ACTION LISTENERS SIMPLIFICADOS ---
        // La vista ya no maneja excepciones. Solo notifica al controlador.
        guardarBtn.addActionListener(e -> controller.cambiarClave());
        cancelarBtn.addActionListener(e -> controller.cancelar());
    }

    public void setController(Controller controller) { this.controller = controller; }
    public JPanel getPanel() { return panel; }

    // Getters para que el Controller lea los datos
    public String getId() { return idFld.getText(); }
    public String getClaveActual() { return new String(claveActualFld.getPassword()); }
    public String getClaveNueva() { return new String(claveNuevaFld.getPassword()); }
    public String getConfirmarClave() { return new String(confirmarClaveFld.getPassword()); }

    // --- MÉTODOS PARA MOSTRAR MENSAJES (controlados por el Controller) ---

    public void showSuccess() {
        JOptionPane.showMessageDialog(panel, "Clave actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método añadido para que el controlador pueda mostrar errores
    public void showError(String message) {
        JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}