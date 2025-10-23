package hospital.protocol.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Receta implements Serializable {
    private String codigo;

    private Paciente paciente;
    private Medico medico;

    // Estos son los campos que SÍ se guardan en el XML
    private String pacienteId;
    private String medicoId;

    private Date fechaConfeccion;
    private Date fechaRetiro;
    private EstadoReceta estado;
    private List<LineaDetalle> lineasDetalle;

    public Receta() {
        this.lineasDetalle = new ArrayList<>();
       // this.fechaConfeccion = new Date();
        this.estado = EstadoReceta.CONFECCIONADA;
    }

    // --- Getters y Setters ---

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    // ======== INICIO DE LA CORRECCIÓN: Métodos que faltaban ========
    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public String getMedicoId() { return medicoId; }
    public void setMedicoId(String medicoId) { this.medicoId = medicoId; }
    // ======== FIN DE LA CORRECCIÓN ========

    public Date getFechaConfeccion() { return fechaConfeccion; }
    public void setFechaConfeccion(Date fechaConfeccion) { this.fechaConfeccion = fechaConfeccion; }

    public Date getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(Date fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public EstadoReceta getEstado() { return estado; }
    public void setEstado(EstadoReceta estado) { this.estado = estado; }

    public List<LineaDetalle> getLineasDetalle() { return lineasDetalle; }
    public void setLineasDetalle(List<LineaDetalle> lineasDetalle) { this.lineasDetalle = lineasDetalle; }
}