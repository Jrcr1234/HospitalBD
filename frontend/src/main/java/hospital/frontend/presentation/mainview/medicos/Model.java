package hospital.frontend.presentation.mainview.medicos;

import hospital.protocol.logic.Medico;
import hospital.frontend.presentation.common.AbstractModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String LIST = "medicos.list";
    public static final String CURRENT = "medicos.current";

    private List<Medico> list;
    private Medico current;

    public Model() {
        this.list = new ArrayList<>();
        this.current = new Medico();
    }

    // --- Métodos para la Lista (Tabla) ---
    public List<Medico> getList() {
        return list;
    }

    public void setList(List<Medico> list) {
        List<Medico> old = this.list;
        this.list = list;
        firePropertyChange(LIST, old, this.list); // Notifica el cambio en la lista
    }

    // --- Métodos para el Medico Actual (Formulario) ---
    public Medico getCurrent() {
        return current;
    }

    public void setCurrent(Medico current) {
        Medico old = this.current;
        this.current = current;
        firePropertyChange(CURRENT, old, this.current); // Notifica el cambio en el formulario
    }
}
