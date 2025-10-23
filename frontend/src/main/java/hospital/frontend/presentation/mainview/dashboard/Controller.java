package hospital.frontend.presentation.mainview.dashboard;

import hospital.protocol.logic.Medicamento;
import hospital.frontend.logic.Service;
// Se elimina la importación de YearMonth, ya que no se usará directamente aquí.
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setModel(model);
        view.setController(this);
    }

    // El método 'show' ahora maneja los errores de carga inicial.
    public void show() {
        try {
            // Le pedimos al Service la lista de todos los medicamentos.
            List<Medicamento> disponibles = Service.getInstance().getMedicamentos();
            model.setMedicamentosDisponibles(disponibles);

            // Generamos los gráficos con los datos iniciales.
            generarReportes();
        } catch (Exception e) {
            // Si no se pueden cargar los medicamentos, se lo informamos al usuario.
            model.setErrorMessage("Error al cargar datos iniciales del dashboard: " + e.getMessage());
        }
    }

    // El método 'generarReportes' ahora maneja los errores de red.
    public void generarReportes() {
        try {
            // --- Gráfico de Pastel ---
            Map<String, Integer> datosPieChart = Service.getInstance().contarRecetasPorEstado();
            view.actualizarGraficoPie(datosPieChart);

            // --- Gráfico de Líneas ---
            Date desde = view.getDesde();
            Date hasta = view.getHasta();
            List<String> nombresMedsSeleccionados = model.getMedicamentosSeleccionados().stream()
                    .map(Medicamento::getNombre)
                    .collect(Collectors.toList());

            if (!nombresMedsSeleccionados.isEmpty() && desde != null && hasta != null) {
                // AHORA ESPERAMOS Map<String, Integer> del Service.
                Map<String, Integer> datosLineChart = Service.getInstance().contarMedicamentosPorMes(desde, hasta, nombresMedsSeleccionados);
                view.actualizarGraficoLineas(datosLineChart);
            } else {
                // Si no hay selección, limpiamos el gráfico.
                view.actualizarGraficoLineas(Map.of());
            }
        } catch (Exception e) {
            // Si falla la generación de reportes, se lo informamos al usuario.
            model.setErrorMessage("Error al generar los reportes: " + e.getMessage());
        }
    }

    // --- Los métodos de manipulación del modelo no cambian ---
    public void agregarMedicamento(Medicamento med) {
        if (med != null) {
            model.agregarMedicamentoSeleccionado(med);
        }
    }

    public void agregarTodos() {
        model.agregarTodosLosMedicamentos();
    }



    public void removerMedicamento(Medicamento med) {
        if (med != null) {
            model.removerMedicamentoSeleccionado(med);
        }
    }
}