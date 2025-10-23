package hospital.frontend.presentation.mainview.dashboard;

import hospital.protocol.logic.Medicamento;
import hospital.frontend.presentation.common.AbstractModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    // Nombres de las "propiedades" para notificar a la Vista cuando algo cambie
    public static final String MEDICAMENTOS_DISPONIBLES = "MEDICAMENTOS_DISPONIBLES";
    public static final String MEDICAMENTOS_SELECCIONADOS = "MEDICAMENTOS_SELECCIONADOS";

    private List<Medicamento> medicamentosDisponibles;
    private List<Medicamento> medicamentosSeleccionados;

    public Model() {
        medicamentosDisponibles = new ArrayList<>();
        medicamentosSeleccionados = new ArrayList<>();
    }

    // --- Getters y Setters que notifican a la Vista ---

    public List<Medicamento> getMedicamentosDisponibles() {
        return medicamentosDisponibles;
    }

    public void setMedicamentosDisponibles(List<Medicamento> disponibles) {
        this.medicamentosDisponibles = disponibles;
        // "Dispara" una notificación para que la Vista se actualice
        firePropertyChange(MEDICAMENTOS_DISPONIBLES, null, disponibles);
    }

    public List<Medicamento> getMedicamentosSeleccionados() {
        return medicamentosSeleccionados;
    }

    public void setMedicamentosSeleccionados(List<Medicamento> seleccionados) {
        this.medicamentosSeleccionados = seleccionados;
        firePropertyChange(MEDICAMENTOS_SELECCIONADOS, null, seleccionados);
    }

    // --- Métodos de ayuda para manipular la lista de seleccionados ---

    public void agregarMedicamentoSeleccionado(Medicamento med) {
        // Solo lo agrega si no está ya en la lista
        if (!medicamentosSeleccionados.contains(med)) {
            List<Medicamento> nuevaLista = new ArrayList<>(this.medicamentosSeleccionados);
            nuevaLista.add(med);
            setMedicamentosSeleccionados(nuevaLista);
        }
    }

    public void agregarTodosLosMedicamentos() {
        setMedicamentosSeleccionados(new ArrayList<>(this.medicamentosDisponibles));
    }

    public void removerMedicamentoSeleccionado(Medicamento med) {
        List<Medicamento> nuevaLista = new ArrayList<>(this.medicamentosSeleccionados);
        nuevaLista.remove(med);
        setMedicamentosSeleccionados(nuevaLista);
    }
}