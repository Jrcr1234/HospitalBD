package hospital.frontend.presentation.mainview.pacientes;

import hospital.protocol.logic.Paciente;
import hospital.frontend.presentation.common.AbstractModel; // <-- Corregido
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel { // <-- Corregido
    // Constantes para identificar las propiedades que cambian
    public static final String LIST = "pacientes.list";
    public static final String CURRENT = "pacientes.current";

    private List<Paciente> list; // <-- Atributo añadido para la tabla
    private Paciente current;    // Atributo para el formulario

    public Model() {
        this.list = new ArrayList<>();
        this.current = new Paciente();
    }

    // --- Métodos para la Lista (Tabla) ---
    public List<Paciente> getList() {
        return list;
    }

    public void setList(List<Paciente> list) {
        List<Paciente> old = this.list;
        this.list = list;
        firePropertyChange(LIST, old, this.list); // Notifica el cambio en la lista
    }

    // --- Métodos para el Paciente Actual (Formulario) ---
    public Paciente getCurrent() {
        return current;
    }

    public void setCurrent(Paciente current) {
        Paciente old = this.current;
        this.current = current;
        firePropertyChange(CURRENT, old, this.current); // Notifica el cambio en el formulario
    }
}