package hospital.protocol.logic;

import java.io.Serializable;

public class Medico extends Usuario  implements Serializable {
    private String especialidad;

    public Medico() {
        super("", "", "", "Medico"); // Llama al constructor del padre
        this.especialidad = "";
    }

    public Medico(String id, String clave, String nombre, String especialidad) {
        super(id, clave, nombre, "Medico");
        this.especialidad = especialidad;
    }

    // --- Getters y Setters para especialidad ---
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}