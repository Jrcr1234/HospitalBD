package hospital.frontend.presentation.prescripcion;

import javax.swing.SwingWorker; // Importamos SwingWorker
import hospital.frontend.application.Application;
import hospital.frontend.logic.Service;
import hospital.frontend.logic.Sesion;
import hospital.protocol.logic.LineaDetalle;
import hospital.protocol.logic.Medico;
import hospital.protocol.logic.Paciente;
import hospital.protocol.logic.Receta;

import javax.swing.JDialog;

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setModel(model);
        view.setController(this);
    }

    public void eliminarMedicamento(int rowIndex) {
        try {
            if (rowIndex < 0) {
                throw new Exception("Debe seleccionar el medicamento a eliminar.");
            }
            model.removeLinea(rowIndex);
        } catch (Exception e) {
            model.setErrorMessage(e.getMessage());
        }
    }

    // Este método es llamado por diálogos, por lo que es mejor que
    // el diálogo mismo maneje el error si devuelve null.
    public LineaDetalle getLineaParaModificar(int index) {
        try {
            if (index < 0) {
                throw new Exception("Debe seleccionar el medicamento a modificar.");
            }
            return model.getLineas().get(index);
        } catch (Exception e) {
            model.setErrorMessage(e.getMessage());
            return null; // Devuelve null si hay un error
        }
    }

    // --- MÉTODO 'registrarReceta' CORREGIDO CON MVC Y SWINGWORKER ---
    public void registrarReceta() {
        try {
            // --- 1. Validaciones locales (sin cambios) ---
            if (model.getPaciente() == null) {
                throw new Exception("Debe seleccionar un paciente.");
            }
            if (model.getLineas().isEmpty()) {
                throw new Exception("La receta debe tener al menos un medicamento.");
            }
            // Eliminamos la validación de fecha de retiro aquí, ya que el backend la pone null
            // if (view.getFechaRetiro() == null) {
            //     throw new Exception("Debe seleccionar una fecha de retiro.");
            // }

            // === CAMBIO 1: El SwingWorker ahora maneja 'Receta', no 'String' ===
            new SwingWorker<Receta, Void>() {
                @Override
                // === CAMBIO 2: doInBackground devuelve 'Receta' ===
                protected Receta doInBackground() throws Exception {
                    // --- 2. Creación del Objeto (sin cambios) ---
                    Receta recetaAGuardar = new Receta();
                    Medico medicoLogueado = (Medico) Sesion.getUsuario();
                    Paciente pacienteSeleccionado = model.getPaciente();

                    recetaAGuardar.setMedico(medicoLogueado);
                    recetaAGuardar.setPaciente(pacienteSeleccionado);
                    recetaAGuardar.setMedicoId(medicoLogueado.getId());
                    recetaAGuardar.setPacienteId(pacienteSeleccionado.getId());
                    // La fecha de confección
                    recetaAGuardar.setFechaConfeccion(view.getFechaConfeccion());
                    recetaAGuardar.setLineasDetalle(model.getLineas());
                    // La fecha de retiro inicial la pone el backend
                    recetaAGuardar.setFechaRetiro(view.getFechaRetiro());

                    // --- 3. Llamada al Servicio (¡Capturamos el resultado!) ---
                    // === CAMBIO 3: Guardamos la receta devuelta por el service ===
                    Receta recetaCreada = Service.getInstance().createReceta(recetaAGuardar);

                    // --- 4. Devolvemos el objeto Receta completo ---
                    // === CAMBIO 4: Devolvemos el objeto, no solo el código ===
                    return recetaCreada;
                }

                @Override
                protected void done() {
                    try {
                        // === CAMBIO 5: 'get()' ahora devuelve el objeto Receta ===
                        Receta recetaConfirmada = get();

                        model.clear(); // Limpia el formulario

                        // === CAMBIO 6: Usamos el código de la receta devuelta ===
                        model.setSuccessMessage("Receta " + recetaConfirmada.getCodigo() + " registrada exitosamente.");

                    } catch (Exception ex) {
                        // Manejo de errores (sin cambios)
                        try {
                            // Intenta obtener la causa raíz del error (más útil)
                            model.setErrorMessage(ex.getCause().getMessage());
                        } catch (Exception e) {
                            // Si no hay causa raíz, muestra el mensaje general
                            model.setErrorMessage(ex.getMessage());
                        }
                    }
                }
            }.execute();

        } catch (Exception e) {
            // Captura los errores de validación y los pasa al modelo (sin cambios)
            model.setErrorMessage(e.getMessage());
        }
    }
    public void limpiar() {
        model.clear();
    }

    // --- LÓGICA FUNCIONAL PARA ABRIR EL DIÁLOGO DE BÚSQUEDA DE PACIENTE (CORREGIDA) ---
    public void buscarPaciente() {
        try {
            JDialog dialog = new JDialog(Application.getWindow(), "Buscar Paciente", true);

            hospital.frontend.presentation.prescripcion.paciente_search.View searchView = new hospital.frontend.presentation.prescripcion.paciente_search.View();
            hospital.frontend.presentation.prescripcion.paciente_search.Model searchModel = new hospital.frontend.presentation.prescripcion.paciente_search.Model();

            // --- ORDEN DE ARGUMENTOS CORREGIDO ---
            // El constructor de paciente_search.Controller es: (View, Model, JDialog, Model)
            new hospital.frontend.presentation.prescripcion.paciente_search.Controller(
                    searchView,
                    searchModel,
                    dialog,       // <--- TERCER argumento es el JDialog
                    this.model    // <--- CUARTO argumento es el Model principal
            );

            searchView.init(); // Es buena práctica inicializar la vista después de crear el controller
            dialog.setContentPane(searchView.getPanel());
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(Application.getWindow());
            dialog.setVisible(true);

        } catch (Exception e) {
            model.setErrorMessage("Error al abrir la búsqueda de pacientes: " + e.getMessage());
        }
    }

    // --- LÓGICA FUNCIONAL PARA ABRIR EL DIÁLOGO DE AGREGAR MEDICAMENTO (CORREGIDA) ---
    public void agregarMedicamento() {
        try {
            JDialog dialog = new JDialog(Application.getWindow(), "Agregar Medicamento", true);

            hospital.frontend.presentation.prescripcion.medicamento_add.View addView = new hospital.frontend.presentation.prescripcion.medicamento_add.View();
            hospital.frontend.presentation.prescripcion.medicamento_add.Model addModel = new hospital.frontend.presentation.prescripcion.medicamento_add.Model();

            // --- ORDEN DE ARGUMENTOS CORREGIDO ---
            // El constructor de medicamento_add.Controller es: (View, Model, JDialog, Model)
            new hospital.frontend.presentation.prescripcion.medicamento_add.Controller(
                    addView,
                    addModel,
                    dialog,       // <--- TERCER argumento es el JDialog
                    this.model    // <--- CUARTO argumento es el Model principal
            );

            addView.init(); // Inicializamos la vista
            dialog.setContentPane(addView.getPanel());
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(Application.getWindow());
            dialog.setVisible(true);

        } catch (Exception e) {
            model.setErrorMessage("Error al abrir el diálogo para agregar medicamentos: " + e.getMessage());
        }
    }
}