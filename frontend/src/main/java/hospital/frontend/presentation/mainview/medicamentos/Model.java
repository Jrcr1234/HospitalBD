package hospital.frontend.presentation.mainview.medicamentos;

import hospital.protocol.logic.Medicamento;
import hospital.frontend.presentation.common.AbstractModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String LIST = "medicamentos.list";
    public static final String CURRENT = "medicamentos.current";

    private List<Medicamento> list;
    private Medicamento current;

    public Model() {
        this.list = new ArrayList<>();
        this.current = new Medicamento();
    }

    public void setList(List<Medicamento> list) {
        List<Medicamento> old = this.list;
        this.list = list;
        firePropertyChange(LIST, old, this.list);
    }

    public void setCurrent(Medicamento current) {
        Medicamento old = this.current;
        this.current = current;
        firePropertyChange(CURRENT, old, this.current);
    }

    public List<Medicamento> getList() { return list; }
    public Medicamento getCurrent() { return current; }
}