package hospital.frontend.presentation.mainview.dashboard;

import com.toedter.calendar.JDateChooser;
import hospital.protocol.logic.Medicamento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Map;

public class View implements PropertyChangeListener {
    // --- Atributos de la Interfaz  ---
    private JPanel panel;
    private JPanel desdePanel;
    private JPanel hastaPanel;
    private JComboBox<Medicamento> medicamentosCmb;
    private JButton addMedBtn;
    private JButton addAllMedsBtn;
    private JTable medicamentosSeleccionadosTbl;
    private JButton removeMedBtn;
    private JButton generarBtn;
    private JPanel pieChartPanel;
    private JPanel lineChartPanel;
    private JPanel filtrosPanel;

    // --- Componentes creados en código  ---
    private JDateChooser desdeDateChooser;
    private JDateChooser hastaDateChooser;

    // --- Variables MVC  ---
    private TableModel tableModel;
    private Controller controller;
    private Model model;

    public void init() {
        // --- Inicialización de Componentes  ---
        desdeDateChooser = new JDateChooser(new Date());
        hastaDateChooser = new JDateChooser(new Date());
        desdePanel.setLayout(new BorderLayout());
        desdePanel.add(desdeDateChooser, BorderLayout.CENTER);
        hastaPanel.setLayout(new BorderLayout());
        hastaPanel.add(hastaDateChooser, BorderLayout.CENTER);

        // --- ActionListeners Simplificados ---
        addMedBtn.addActionListener(e -> controller.agregarMedicamento((Medicamento) medicamentosCmb.getSelectedItem()));
        addAllMedsBtn.addActionListener(e -> controller.agregarTodos());
        removeMedBtn.addActionListener(e -> {
            int selectedRow = medicamentosSeleccionadosTbl.getSelectedRow();
            if (selectedRow >= 0) {
                // Obtenemos el medicamento del modelo para asegurar consistencia
                Medicamento medParaRemover = model.getMedicamentosSeleccionados().get(selectedRow);
                controller.removerMedicamento(medParaRemover);
            }
        });
        generarBtn.addActionListener(e -> controller.generarReportes());
    }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        tableModel = new TableModel(model.getMedicamentosSeleccionados());
        medicamentosSeleccionadosTbl.setModel(tableModel);
    }

    // === PROPERTYCHANGE AHORA MANEJA TODAS LAS ACTUALIZACIONES ===
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        // 1. Escucha por errores y los muestra
        if (evt.getPropertyName().equals("errorMessage")) {
            String error = (String) evt.getNewValue();
            if (error != null && !error.isEmpty()) {
                JOptionPane.showMessageDialog(panel, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 2. Actualiza el ComboBox de medicamentos disponibles
        if (evt.getPropertyName().equals(Model.MEDICAMENTOS_DISPONIBLES)) {
            DefaultComboBoxModel<Medicamento> comboBoxModel = new DefaultComboBoxModel<>(model.getMedicamentosDisponibles().toArray(new Medicamento[0]));
            medicamentosCmb.setModel(comboBoxModel);
        }

        // 3. Actualiza la tabla de medicamentos seleccionados
        if (evt.getPropertyName().equals(Model.MEDICAMENTOS_SELECCIONADOS)) {
            tableModel.setRows(model.getMedicamentosSeleccionados());
        }
    }

    public JPanel getPanel() { return panel; }
    public Date getDesde() { return desdeDateChooser.getDate(); }
    public Date getHasta() { return hastaDateChooser.getDate(); }

    // --- MÉTODOS DE ACTUALIZACIÓN DE GRÁFICOS (actualizados) ---

    public void actualizarGraficoPie(Map<String, Integer> datos) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (datos != null) {
            datos.forEach(dataset::setValue);
        }
        JFreeChart pieChart = ChartFactory.createPieChart("Recetas por Estado", dataset, true, true, false);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("CONFECCIONADA", new Color(0x71BCF2));
        plot.setSectionPaint("PROCESO", new Color(0xF5BD63));
        plot.setSectionPaint("LISTA", new Color(0x86DB86));
        plot.setSectionPaint("ENTREGADA", new Color(0xBCBCBC));
        ChartPanel chartPanel = new ChartPanel(pieChart);
        pieChartPanel.removeAll();
        pieChartPanel.setLayout(new BorderLayout());
        pieChartPanel.add(chartPanel, BorderLayout.CENTER);
        pieChartPanel.revalidate();
        pieChartPanel.repaint();
    }

    // === 'actualizarGraficoLineas' CORREGIDO PARA ACEPTAR Map<String, Integer> ===
    public void actualizarGraficoLineas(Map<String, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (data != null) {
            // El bucle ahora trabaja con una clave de tipo String.
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                // La clave ya es un String ("AAAA-MM"), por lo que se usa directamente.
                dataset.addValue(entry.getValue(), "Cantidad", entry.getKey());
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Medicamentos Prescritos por Mes",
                "Mes (Año-Mes)", // Etiqueta del eje actualizada
                "Cantidad Total",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(560, 367));
        lineChartPanel.removeAll();
        lineChartPanel.setLayout(new BorderLayout());
        lineChartPanel.add(chartPanel, BorderLayout.CENTER);
        lineChartPanel.revalidate();
        lineChartPanel.repaint();
    }
}