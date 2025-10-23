package hospital.protocol.logic;

import java.io.Serializable;

public class Farmaceuta extends Usuario implements Serializable {

    public Farmaceuta() {
        super("", "", "", "Farmaceuta");
    }

    public Farmaceuta(String id, String clave, String nombre) {
        super(id, clave, nombre, "Farmaceuta");
    }
}