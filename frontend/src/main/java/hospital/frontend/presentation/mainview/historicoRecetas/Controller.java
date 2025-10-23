package hospital.frontend.presentation.mainview.historicoRecetas;

import hospital.protocol.logic.Receta;
import hospital.frontend.logic.Service;

// Se elimina la importación de JOptionPane
import java.util.List;

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.model = model;
        this.view = view;

        // Conexión MVC
        view.setModel(model);
        view.setController(this);

        // Cargar datos iniciales
       // this.search(""); // Llama al método search que ya maneja errores
    }

    // === 'search' CORREGIDO CON MANEJO DE ERRORES MVC ===
    public void search(String filtro) {
        try {
            List<Receta> rows;
            if (filtro == null || filtro.isEmpty()) {
                rows = Service.getInstance().getRecetas(); // Puede lanzar excepción
            } else {
                rows = Service.getInstance().findRecetasHistorico(filtro); // Puede lanzar excepción
            }
            model.setList(rows);
        } catch (Exception e) {
            // Si hay un error de red, se lo pasamos al modelo.
            model.setErrorMessage(e.getMessage());
        }
    }

    // === 'verDetalle' CORREGIDO PARA NO USAR JOPTIONPANE ===
    public void verDetalle(int row) {
        try {
            Receta receta = model.getList().get(row);
            model.setCurrent(receta);
            // En lugar de mostrar un JOptionPane, se podría tener un área de texto en la Vista
            // que se actualice cuando 'current' cambia.
            // O, si realmente se quiere un pop-up, la Vista debería crearlo
            // cuando se notifica un cambio, por ejemplo, con una nueva propiedad en el modelo.
            // Por ahora, solo actualizamos el 'current'. La vista decidirá qué hacer.

        } catch (IndexOutOfBoundsException e) {
            model.setErrorMessage("La receta seleccionada ya no es válida.");
        }
    }
}