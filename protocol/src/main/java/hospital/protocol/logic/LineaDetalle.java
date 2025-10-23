package hospital.protocol.logic;

import java.io.Serializable;

public class LineaDetalle implements Serializable {
    private Medicamento medicamento;
    private int cantidad;
    private String indicaciones;
    private int duracionTratamiento; // en días

    public LineaDetalle() {
        this.medicamento = null;
        this.cantidad = 0;
        this.indicaciones = "";
        this.duracionTratamiento = 0;
    }

    public LineaDetalle(Medicamento medicamento, int cantidad, String indicaciones, int duracionTratamiento) {
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
        this.duracionTratamiento = duracionTratamiento;
    }

    // --- Getters y Setters para todos los atributos ---
    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
    public int getDuracionTratamiento() { return duracionTratamiento; }
    public void setDuracionTratamiento(int duracionTratamiento) { this.duracionTratamiento = duracionTratamiento; }
}