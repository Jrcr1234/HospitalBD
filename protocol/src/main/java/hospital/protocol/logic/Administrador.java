package hospital.protocol.logic;

import java.io.Serializable;

public class Administrador extends Usuario implements Serializable {

    public Administrador() {
        super("", "", "", "Administrador");
    }

    public Administrador(String id, String clave, String nombre) {
        super(id, clave, nombre, "Administrador");
    }
}