package hospital.frontend.presentation.mainview;

import hospital.frontend.logic.Sesion;
import hospital.frontend.presentation.mainview.historicoRecetas.Controller;
import hospital.frontend.presentation.mainview.historicoRecetas.Model;
import hospital.protocol.logic.Usuario;
import hospital.frontend.presentation.util.GuiUtils;
import javax.swing.*;

public class View {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private JButton chpassBtn;

    public View() {
        Usuario usuario = Sesion.getUsuario();
        if (usuario == null) return;
        int tabIconSize = 16;

        try {
            // =====================================================================
            // PASO 1: INICIALIZAR TODOS LOS MÓDULOS INDEPENDIENTEMENTE DEL ROL
            // =====================================================================

            // --- Módulos de Administrador ---
            hospital.frontend.presentation.mainview.medicamentos.Model medModel = new hospital.frontend.presentation.mainview.medicamentos.Model();
            hospital.frontend.presentation.mainview.medicamentos.View medView = new hospital.frontend.presentation.mainview.medicamentos.View();
            new hospital.frontend.presentation.mainview.medicamentos.Controller(medView, medModel);
            medView.init();

            hospital.frontend.presentation.mainview.pacientes.Model pacModel = new hospital.frontend.presentation.mainview.pacientes.Model();
            hospital.frontend.presentation.mainview.pacientes.View pacView = new hospital.frontend.presentation.mainview.pacientes.View();
            new hospital.frontend.presentation.mainview.pacientes.Controller(pacView, pacModel);
            pacView.init();

            hospital.frontend.presentation.mainview.farmaceutas.Model farModel = new hospital.frontend.presentation.mainview.farmaceutas.Model();
            hospital.frontend.presentation.mainview.farmaceutas.View farView = new hospital.frontend.presentation.mainview.farmaceutas.View();
            new hospital.frontend.presentation.mainview.farmaceutas.Controller(farView, farModel);
            farView.init();

            hospital.frontend.presentation.mainview.medicos.Model medicosModel = new hospital.frontend.presentation.mainview.medicos.Model();
            hospital.frontend.presentation.mainview.medicos.View medicosView = new hospital.frontend.presentation.mainview.medicos.View();
            new hospital.frontend.presentation.mainview.medicos.Controller(medicosView, medicosModel);
            medicosView.init();

            // --- Módulo de Médico ---
            hospital.frontend.presentation.prescripcion.Model prescModel = new hospital.frontend.presentation.prescripcion.Model();
            hospital.frontend.presentation.prescripcion.View prescView = new hospital.frontend.presentation.prescripcion.View();
            new hospital.frontend.presentation.prescripcion.Controller(prescView, prescModel);
            prescView.init();

            // --- Módulo de Farmaceuta ---
            hospital.frontend.presentation.despacho.Model despachoModel = new hospital.frontend.presentation.despacho.Model();
            hospital.frontend.presentation.despacho.View despachoView = new hospital.frontend.presentation.despacho.View();
            hospital.frontend.presentation.despacho.Controller despachoController = new hospital.frontend.presentation.despacho.Controller(despachoView, despachoModel);
            despachoView.init();

            // --- Módulos Comunes ---
            Model historicoModel = new Model();
            hospital.frontend.presentation.mainview.historicoRecetas.View historicoView = new hospital.frontend.presentation.mainview.historicoRecetas.View();
            new Controller(historicoView, historicoModel);
            historicoView.init();

            hospital.frontend.presentation.mainview.dashboard.Model dashboardModel = new hospital.frontend.presentation.mainview.dashboard.Model();
            hospital.frontend.presentation.mainview.dashboard.View dashboardView = new hospital.frontend.presentation.mainview.dashboard.View();
            // Guardamos la referencia al controller para usarla después
            hospital.frontend.presentation.mainview.dashboard.Controller dashboardController =
                    new hospital.frontend.presentation.mainview.dashboard.Controller(dashboardView, dashboardModel);

            // 1. Primero inicializamos la VISTA
            dashboardView.init();

            // 2. LUEGO le pedimos al CONTROLLER que cargue los datos
            dashboardController.show();

            hospital.frontend.presentation.mainview.about.View aboutView = new hospital.frontend.presentation.mainview.about.View();


            // =====================================================================
            // PASO 2: AÑADIR PESTAÑAS SEGÚN EL ROL DEL USUARIO
            // =====================================================================

            tabbedPane.removeAll(); // Limpiamos para empezar de cero

            // --- Pestañas Específicas del Rol ---
            if (usuario.getTipo().equals("Administrador")) {
                ImageIcon medicamentosIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/medicamento.png")), tabIconSize, tabIconSize);
                tabbedPane.addTab("Medicamentos", medicamentosIcon, medView.getPanel());

                ImageIcon pacientesIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/paciente.png")), tabIconSize, tabIconSize);
                tabbedPane.addTab("Pacientes", pacientesIcon, pacView.getPanel());

                ImageIcon farmaceutasIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/farmaceuta.png")), tabIconSize, tabIconSize);
                tabbedPane.addTab("Farmaceutas", farmaceutasIcon, farView.getPanel());

                ImageIcon medicosIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/medico.png")), tabIconSize, tabIconSize);
                tabbedPane.addTab("Médicos", medicosIcon, medicosView.getPanel());
            }

            if (usuario.getTipo().equals("Medico")) {
                ImageIcon prescIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/prescripcion.png")), tabIconSize, tabIconSize);
                tabbedPane.addTab("Prescribir Receta", prescIcon, prescView.getPanel());
            }

            if (usuario.getTipo().equals("Farmaceuta")) {
                ImageIcon despachoIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/farmaceuta.png")), tabIconSize, tabIconSize);
                tabbedPane.addTab("Despacho", despachoIcon, despachoView.getPanel());
                despachoController.show();
            }

            // --- Pestañas Comunes para TODOS los roles ---
            ImageIcon dashboardIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/reporte.png")), tabIconSize, tabIconSize);
            tabbedPane.addTab("Dashboard", dashboardIcon, dashboardView.getPanel());

            ImageIcon historicoIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/receta.png")), tabIconSize, tabIconSize);
            tabbedPane.addTab("Histórico", historicoIcon, historicoView.getPanel());

            ImageIcon aboutIcon = GuiUtils.scaleIcon(new ImageIcon(getClass().getResource("/icons/info.png")), tabIconSize, tabIconSize);
            tabbedPane.addTab("Acerca de", aboutIcon, aboutView.getPanel());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}