package hospital.frontend.presentation.prescripcion.paciente_search;

import hospital.protocol.logic.Paciente;
import hospital.frontend.presentation.common.AbstractModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String LIST = "paciente_search.list";
    private List<Paciente> list;

    public Model() {
        this.list = new ArrayList<>();
    }

    public List<Paciente> getList() { return list; }
    public void setList(List<Paciente> list) {
        this.list = list;
        firePropertyChange(LIST, null, this.list);
    }
}