package hospital.frontend.presentation.prescripcion.medicamento_add;

import hospital.protocol.logic.Medicamento;
import hospital.frontend.presentation.common.AbstractModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String LIST = "medicamento_add.list";
    public static final String CURRENT = "medicamento_add.current";

    private List<Medicamento> list;
    private Medicamento current;

    public Model() {
        this.list = new ArrayList<>();
        this.current = null; // Inicia sin ningún medicamento seleccionado
    }

    public List<Medicamento> getList() { return list; }
    public void setList(List<Medicamento> list) {
        this.list = list;
        firePropertyChange(LIST, null, this.list);
    }

    public Medicamento getCurrent() { return current; }
    public void setCurrent(Medicamento current) {
        this.current = current;
        firePropertyChange(CURRENT, null, this.current);
    }
}