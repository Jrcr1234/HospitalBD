package hospital.frontend.presentation.login;

import hospital.protocol.logic.Usuario;
import hospital.frontend.presentation.common.AbstractModel;

public class Model extends AbstractModel {
    private Usuario current;

    public Model() {
        this.current = new Usuario() {}; // Instancia anónima de la clase abstracta
    }

    public Usuario getCurrent() { return current; }
    public void setCurrent(Usuario current) { this.current = current; }
}