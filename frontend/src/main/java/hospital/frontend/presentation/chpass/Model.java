package hospital.frontend.presentation.chpass;

import hospital.protocol.logic.Usuario;

public class Model {
    private Usuario user;

    // --- EL CONSTRUCTOR QUE FALTABA ---
    public Model(Usuario user) {
        this.user = user;
    }

    public Usuario getUser() {
        return user;
    }
}