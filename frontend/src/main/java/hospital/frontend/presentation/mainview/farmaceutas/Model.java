package hospital.frontend.presentation.mainview.farmaceutas;

import hospital.protocol.logic.Farmaceuta;
import hospital.frontend.presentation.common.AbstractModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String LIST = "farmaceutas.list";
    public static final String CURRENT = "farmaceutas.current";

    private List<Farmaceuta> list;
    private Farmaceuta current;

    public Model() {
        this.list = new ArrayList<>();
        this.current = new Farmaceuta();
    }

    // --- Métodos para la Lista (Tabla) ---
    public List<Farmaceuta> getList() {
        return list;
    }

    public void setList(List<Farmaceuta> list) {
        List<Farmaceuta> old = this.list;
        this.list = list;
        firePropertyChange(LIST, old, this.list); // Notifica el cambio en la lista
    }

    // --- Métodos para el Medico Actual (Formulario) ---
    public Farmaceuta getCurrent() {
        return current;
    }

    public void setCurrent(Farmaceuta current) {
        Farmaceuta old = this.current;
        this.current = current;
        firePropertyChange(CURRENT, old, this.current); // Notifica el cambio en el formulario
    }
}
