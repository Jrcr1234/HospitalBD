package hospital.protocol.logic;

import java.io.Serializable;
import java.util.Objects;

public class Medicamento implements Serializable {
    private String codigo;
    private String nombre;
    private String presentacion;

    public Medicamento() {
        this.codigo = "";
        this.nombre = "";
        this.presentacion = "";
    }

    public Medicamento(String codigo, String nombre, String presentacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    @Override
    public String toString() {
        // Devuelve el nombre del medicamento.
        // Esto es lo que el JComboBox usará para mostrar cada ítem.
        return this.nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicamento that = (Medicamento) o;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    // --- Getters y Setters para todos los atributos ---
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
}