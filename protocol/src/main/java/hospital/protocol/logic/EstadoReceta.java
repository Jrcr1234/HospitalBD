package hospital.protocol.logic;

/**
 * Representa los estados posibles de una receta en el sistema.
 */
public enum EstadoReceta {
    CONFECCIONADA,  // Recién creada por el médico
    PROCESO,        // Farmaceuta la toma para prepararla
    LISTA,          // Medicamentos alistados, lista para ser retirada
    ENTREGADA       // Paciente retiró los medicamentos
}