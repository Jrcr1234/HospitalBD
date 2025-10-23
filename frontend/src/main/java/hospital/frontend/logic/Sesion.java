package hospital.frontend.logic; // O hospital.application

import hospital.protocol.logic.Usuario;

public class Sesion {
    private static Usuario usuario;

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        Sesion.usuario = usuario;
    }
}