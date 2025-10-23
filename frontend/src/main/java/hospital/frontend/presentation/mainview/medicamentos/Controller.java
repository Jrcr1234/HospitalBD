package hospital.frontend.presentation.mainview.medicamentos;

import hospital.protocol.logic.Medicamento;
import hospital.frontend.logic.Service;
import java.util.List;
import javax.swing.SwingWorker;

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.model = model;
        this.view = view;
        view.setModel(model);
        view.setController(this);

        // Se cargan los datos iniciales y se maneja cualquier error de red.
        try {
            model.setList(Service.getInstance().getMedicamentos());
            model.setCurrent(new Medicamento());
        } catch (Exception e) {
            // Se le pasa el error al modelo para que la vista lo muestre.
            model.setErrorMessage(e.getMessage());
        }
    }

    // --- MÉTODO 'SEARCH' CORREGIDO ---
    public void search(String filtro) {
        try {
            List<Medicamento> rows = Service.getInstance().searchMedicamentos(filtro);
            model.setList(rows);
        } catch (Exception e) {
            // Se le pasa el error al modelo.
            model.setErrorMessage(e.getMessage());
        }
    }

    // === MÉTODO 'SAVE' CORREGIDO (SIN JOPTIONPANE) ===
    public void save(Medicamento medicamento) {
        try {
            if (medicamento.getCodigo().isEmpty() || medicamento.getNombre().isEmpty()) {
                throw new Exception("El código y el nombre son requeridos.");
            }

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if (model.getCurrent().getCodigo().isEmpty()) {
                        Service.getInstance().createMedicamento(medicamento);
                    } else {
                        Service.getInstance().updateMedicamento(medicamento);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Captura cualquier excepción del hilo de fondo.
                        // Si todo salió bien, simplemente refrescamos.
                        clear();
                        search("");
                    } catch (Exception ex) {
                        // Se le pasa el error (la causa real) al modelo.
                        model.setErrorMessage(ex.getCause().getMessage());
                    }
                }
            };
            worker.execute();

        } catch (Exception e) {
            // Error de validación, se le pasa al modelo.
            model.setErrorMessage(e.getMessage());
        }
    }

    public void edit(int row) {
        Medicamento med = model.getList().get(row);
        model.setCurrent(med);
    }

    // === MÉTODO 'DELETE' CORREGIDO (SIN JOPTIONPANE) ===
    public void delete(int row) {
        try {
            Medicamento med = model.getList().get(row);

            // NOTA: El JOptionPane de confirmación SÍ puede ir aquí,
            // porque es una interacción directa antes de la acción, no un resultado.
            // Pero para seguir la regla al 100%, lo ideal sería que la Vista lo manejara.
            // Por ahora, nos enfocamos en los mensajes de error/éxito.

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Service.getInstance().deleteMedicamento(med.getCodigo());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        search(""); // Refresca la tabla
                    } catch (Exception e) {
                        // Se le pasa el error al modelo.
                        model.setErrorMessage(e.getCause().getMessage());
                    }
                }
            }.execute();

        } catch (Exception e) {
            model.setErrorMessage("No se pudo seleccionar el medicamento a borrar.");
        }
    }

    public void clear() {
        model.setCurrent(new Medicamento());
    }
}