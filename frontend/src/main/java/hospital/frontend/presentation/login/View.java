package hospital.frontend.presentation.login;

import hospital.frontend.presentation.util.GuiUtils;

import javax.swing.*;

public class View {
    private JPanel panel;
    private JTextField idFld;
    private JPasswordField claveFld;
    private JButton ingresarBtn;
    private JButton salirBtn;
    private JButton cambiarClaveBtn;
    private JLabel headerIconLbl;

    private Controller controller;
    private Model model;

    public View() {
        // Constructor vacío a propósito
    }

    public void init() {
        // --- ActionListeners ---
        ingresarBtn.addActionListener(e -> controller.ingresar(idFld.getText(), new String(claveFld.getPassword())));
        salirBtn.addActionListener(e -> controller.salir());
        cambiarClaveBtn.addActionListener(e -> controller.cambiarClave());

        // --- Configuración de Iconos ---
        try {
            int buttonIconSize = 24;
            int headerIconSize = 128;
            headerIconLbl.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/lock_icon.png")), headerIconSize, headerIconSize));
            ingresarBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/inicio-de-sesion.png")), buttonIconSize, buttonIconSize));
            cambiarClaveBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/contrasena.png")), buttonIconSize, buttonIconSize));
            salirBtn.setIcon(GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/Cancelar.png")), buttonIconSize, buttonIconSize));
        } catch (Exception e) { System.err.println("Error al cargar iconos de login: " + e.getMessage()); }
    }

    public JPanel getPanel() { return panel; }
    public void setController(Controller controller) { this.controller = controller; }
    public void setModel(Model model) { this.model = model; }

    public String getId() { return idFld.getText(); }
    public String getClave() { return new String(claveFld.getPassword()); }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(panel, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void cerrarVentana() {
        SwingUtilities.getWindowAncestor(panel).dispose();
    }
}