package hospital.frontend.presentation.mainview.about;

import javax.swing.*;
import java.awt.Image; // El import necesario para redimensionar

public class View {
    private JPanel panel;
    private JLabel logoLbl; // La variable que se enlaza al JLabel del .form


    public View() {



        // --- CÓDIGO PARA ASIGNAR Y ESCALAR LA IMAGEN ---

        // 1. Carga la imagen original (la grande)
        ImageIcon originalLogo = new ImageIcon(getClass().getResource("/icons/hospi.png"));

        // 2. Redimensiónar a nuestro gusto (ej. 150x150)
        ImageIcon logoEscalado = scaleIcon(originalLogo, 800, 480);

        // 3. Asigna el icono YA REDIMENSIONADO al JLabel
        logoLbl.setIcon(logoEscalado);

        // --- FIN DEL CÓDIGO DE LA IMAGEN ---
    }

    public JPanel getPanel() {
        return panel;
    }

    // --- MÉTODO AYUDANTE PARA REDIMENSIONAR ICONOS ---
    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}