package hospital.frontend.presentation.despacho;

import hospital.protocol.logic.Receta;
import hospital.frontend.presentation.common.AbstractModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String RECETAS = "despacho.recetas";

    private List<Receta> recetas;

    public Model() {
        this.recetas = new ArrayList<>();
    }

    public List<Receta> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<Receta> recetas) {
        this.recetas = recetas;
        // Notificamos a la vista que la lista de recetas ha cambiado
        firePropertyChange(RECETAS, null, this.recetas);
    }
}