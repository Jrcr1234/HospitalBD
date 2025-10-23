package hospital.protocol.logic;

import java.io.Serializable;

public class Usuario implements Serializable {
    protected String id;
    protected String clave;
    protected String nombre;
    protected String tipo;

    public Usuario() {
        this.id = "";
        this.clave = "";
        this.nombre = "";
        this.tipo = "";
    }

    public Usuario(String id, String clave, String nombre, String tipo) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    // --- Getters y Setters ---
    // Métodos para acceder y modificar los atributos de forma controlada.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}