package hospital.frontend.presentation.prescripcion.dosis_details;

import hospital.protocol.logic.Medicamento;
import hospital.protocol.logic.LineaDetalle;
import hospital.frontend.presentation.common.AbstractModel;

// Hacemos que herede de AbstractModel para obtener el manejo de errores
public class Model extends AbstractModel {
    private Medicamento seleccionado;
    private LineaDetalle lineaExistente;

    // Constructor para AGREGAR un nuevo medicamento
    public Model(Medicamento seleccionado) {
        this.seleccionado = seleccionado;
        this.lineaExistente = null;
    }

    // Constructor para MODIFICAR una línea existente
    public Model(LineaDetalle linea) {
        this.seleccionado = linea.getMedicamento();
        this.lineaExistente = linea;
    }

    public Medicamento getSeleccionado() { return seleccionado; }
    public LineaDetalle getLineaExistente() { return lineaExistente; }

    // No es necesario añadir setErrorMessage, ¡ya lo hereda de AbstractModel!
}