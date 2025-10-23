package hospital.frontend.presentation.mainview.historicoRecetas;

import hospital.protocol.logic.Receta;

import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class Model {
    private List<Receta> list;
    private Receta current;
    private final PropertyChangeSupport support;

    private String errorMessage;

    public Model() {
        this.list = new ArrayList<>();
        this.current = new Receta();
        this.support = new PropertyChangeSupport(this);
        this.errorMessage = ""; // Inicializarlo vacío
    }

    public List<Receta> getList() {
        return list;
    }

    public void setList(List<Receta> list) {
        List<Receta> oldList = this.list;
        this.list = list;
        // Notifica que la propiedad "list" ha cambiado.
        support.firePropertyChange("list", oldList, this.list);
    }

    public Receta getCurrent() {
        return current;
    }

    public void setCurrent(Receta current) {
        Receta oldCurrent = this.current;
        this.current = current;
        // Notifica que la propiedad "current" ha cambiado.
        support.firePropertyChange("current", oldCurrent, this.current);
    }


    public void setErrorMessage(String errorMessage) {
        String oldErrorMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        // Notifica a los listeners que la propiedad "errorMessage" ha cambiado.
        support.firePropertyChange("errorMessage", oldErrorMessage, errorMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}