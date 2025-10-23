package hospital.frontend.presentation.util;

import javax.swing.ImageIcon;
import java.awt.Image;

public class GuiUtils {

    /**
     * Redimensiona un ImageIcon a un tamaño específico.
     * Es 'public static' para que pueda ser llamado desde cualquier lugar
     * sin necesidad de crear una instancia de GuiUtils.
     */
    public static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}