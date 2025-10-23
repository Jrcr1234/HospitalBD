package hospital.frontend.presentation.prescripcion;

import hospital.frontend.presentation.common.AbstractModel;
import hospital.protocol.logic.LineaDetalle;
import hospital.protocol.logic.Paciente;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    // Nombres de las propiedades para los eventos
    public static final String PACIENTE = "prescripcion.paciente";
    public static final String LINEAS = "prescripcion.lineas";

    // --- NUEVA PROPIEDAD PARA MENSAJES DE ÉXITO ---
    private String successMessage = "";

    private Paciente paciente;
    private List<LineaDetalle> lineas;

    public Model() {
        this.paciente = null;
        this.lineas = new ArrayList<>();
    }

    public void clear() {
        setPaciente(null);
        setLineas(new ArrayList<>());
    }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) {
        Paciente old = this.paciente;
        this.paciente = paciente;
        firePropertyChange(PACIENTE, old, this.paciente);
    }

    public List<LineaDetalle> getLineas() { return lineas; }
    public void setLineas(List<LineaDetalle> lineas) {
        List<LineaDetalle> old = this.lineas;
        this.lineas = lineas;
        firePropertyChange(LINEAS, old, this.lineas);
    }

    // --- NUEVO MÉTODO PARA NOTIFICAR ÉXITO ---
    public void setSuccessMessage(String successMessage) {
        String oldSuccessMessage = this.successMessage;
        this.successMessage = successMessage;
        firePropertyChange("successMessage", oldSuccessMessage, successMessage);
    }

    // --- MÉTODO ÚTIL PARA AÑADIR LÍNEAS UNA POR UNA ---
    public void addLinea(LineaDetalle linea) {
        List<LineaDetalle> old = new ArrayList<>(this.lineas);
        this.lineas.add(linea);
        firePropertyChange(LINEAS, old, this.lineas);
    }

    public void removeLinea(int index) {
        if (index >= 0 && index < lineas.size()) {
            List<LineaDetalle> old = new ArrayList<>(this.lineas);
            lineas.remove(index);
            firePropertyChange(LINEAS, old, this.lineas);
        }
    }
}