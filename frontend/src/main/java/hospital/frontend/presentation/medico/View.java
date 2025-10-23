package hospital.frontend.presentation.medico;

import hospital.frontend.logic.Sesion;
import hospital.frontend.presentation.prescripcion.Controller;
import hospital.frontend.presentation.prescripcion.Model;
import hospital.frontend.presentation.util.GuiUtils;
import hospital.protocol.logic.Usuario;

import javax.swing.*;

public class View {
    private JPanel panel;
    private JTabbedPane tabbedPane;

    public View() {
        Usuario usuario = Sesion.getUsuario();
        if (usuario == null) return;

        try {
            int tabIconSize = 16;

            // --- PESTAÑA DE PRESCRIPCIÓN ---
            // Creamos el MVC de Prescripción
            Model prescripcionModel = new Model();
            hospital.frontend.presentation.prescripcion.View prescripcionView = new hospital.frontend.presentation.prescripcion.View();
            new Controller(prescripcionView, prescripcionModel);
            prescripcionView.init();

            // Creamos su icono y la añadimos como una pestaña
            ImageIcon prescripcionIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/prescripcion.png")), tabIconSize, tabIconSize);
            tabbedPane.addTab("Prescribir Receta", prescripcionIcon, prescripcionView.getPanel());

            // --- PESTAÑA ACERCA DE ---
            // 1. Creamos la vista de "Acerca de"
            // Reutilizamos la que está en el paquete 'admin' porque es genérica
            hospital.frontend.presentation.mainview.about.View aboutView = new hospital.frontend.presentation.mainview.about.View();

            // 2. Creamos su icono y la añadimos
            ImageIcon aboutIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/info.png")), tabIconSize, tabIconSize);
            tabbedPane.addTab("Acerca de", aboutIcon, aboutView.getPanel());
            // Aquí añadiríamos las pestañas de Dashboard e Histórico cuando las creemos...

        } catch (Exception e) {
            System.err.println("Error al crear las pestañas del médico: " + e.getMessage());
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}