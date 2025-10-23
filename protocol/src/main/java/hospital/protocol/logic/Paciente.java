package hospital.protocol.logic;

import java.io.Serializable;
import java.util.Date;

public class Paciente implements Serializable {
    private String id;
    private String nombre;
    private Date fechaNacimiento;
    private String telefono;

    public Paciente() {
        this.id = "";
        this.nombre = "";
        this.fechaNacimiento = null;
        this.telefono = "";
    }

    public Paciente(String id, String nombre, Date fechaNacimiento, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
    }

    // GETTERS Y SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}