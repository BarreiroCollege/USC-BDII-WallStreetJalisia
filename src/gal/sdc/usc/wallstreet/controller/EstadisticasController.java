package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Estadistica;
import gal.sdc.usc.wallstreet.repository.EstadisticasDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class EstadisticasController extends DatabaseLinker {
    public static final String VIEW = "estadisticas";
    public static final Integer HEIGHT = 650;
    public static final Integer WIDTH = 900;
    public static final String TITULO = "Estadísticas";


    @FXML
    private BarChart<String, Float> barChartBeneficioMedio;
    @FXML
    private CategoryAxis ejeXBeneficioMedio;
    @FXML
    private BarChart<String, Float> barChartPartMedias;
    @FXML
    private CategoryAxis ejeXPartMedias;
    @FXML
    private BarChart<String, Integer> barChartNumPagos;
    @FXML
    private CategoryAxis ejeXNumPagos;
    @FXML
    private BarChart<String, Float> barChartPrecioMedio;
    @FXML
    private CategoryAxis ejeXPrecioMedio;

    private ObservableList<Estadistica> estadisticas = FXCollections.observableArrayList();
    private ObservableList<Estadistica> estadBeneficioMedio = FXCollections.observableArrayList();
    private ObservableList<Estadistica> estadPartMedias = FXCollections.observableArrayList();
    private ObservableList<Estadistica> estadNumPagos = FXCollections.observableArrayList();
    private ObservableList<Estadistica> estadPrecioMedio = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Obtener datos estadísticos sobre las empresas
        construirDatos();
        // Se indican los valores del eje X
        construirEjesX();
        // Valores eje Y
        construirEjesY();
        // Personalización
        personalizarDiagramas();
    }

    private void construirDatos() {
        // Se obtienen todos los datos de la materialized view
        estadisticas.addAll(super.getDAO(EstadisticasDAO.class).getDatos());

        // Para cada diagrama de barras, se toman las 5 empresas más destacadas
        // Dado que los valores nulos en floats de la base de datos se guardan como 0 en Java, se comprueba que el valor
        // absoluto del atributo de interés no sea muy próximo a 0. En el caso de enteros, la verificación es directa.
        estadBeneficioMedio = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getBeneficioMedio).reversed())
                .filter(estad -> Math.abs(estad.getBeneficioMedio()) > 0.0001)
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
        estadPartMedias = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getParticipacionesMedias).reversed())
                .filter(estad -> Math.abs(estad.getParticipacionesMedias()) > 0.0001)
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
        estadNumPagos = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getNumPagosMes).reversed())
                .filter(estad -> Math.abs(estad.getNumPagosMes()) != 0)
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
        estadPrecioMedio = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getPrecioMedioMes).reversed())
                .filter(estad -> Math.abs(estad.getPrecioMedioMes()) > 0.0001)
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void construirEjesX() {
        // Para cada diagrama, se muestra el identificador de la empresa
        ejeXBeneficioMedio.setCategories(estadBeneficioMedio.stream().map(Estadistica::getIdentificadorEmpresa)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        ejeXPartMedias.setCategories(estadPartMedias.stream().map(Estadistica::getIdentificadorEmpresa)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        ejeXNumPagos.setCategories(estadNumPagos.stream().map(Estadistica::getIdentificadorEmpresa)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        ejeXPrecioMedio.setCategories(estadPrecioMedio.stream().map(Estadistica::getIdentificadorEmpresa)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private void construirEjesY() {
        XYChart.Series<String, Float> serieBenMedio = new XYChart.Series<>();
        XYChart.Series<String, Float> seriePartMedias = new XYChart.Series<>();
        XYChart.Series<String, Integer> serieNumPagos = new XYChart.Series<>();
        XYChart.Series<String, Float> seriePrecioMedio = new XYChart.Series<>();

        // Se construye un XYChart.Data para cada empresa de cada diagrama y es añade a su serie
        for (Estadistica estad : estadBeneficioMedio) {
            serieBenMedio.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getBeneficioMedio()));
        }
        for (Estadistica estad : estadPartMedias) {
            seriePartMedias.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getParticipacionesMedias()));
        }
        for (Estadistica estad : estadNumPagos) {
            serieNumPagos.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getNumPagosMes()));
        }
        for (Estadistica estad : estadPrecioMedio) {
            seriePrecioMedio.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getPrecioMedioMes()));
        }

        // Se conectan los datos a los diagramas
        barChartBeneficioMedio.getData().add(serieBenMedio);
        barChartPartMedias.getData().add(seriePartMedias);
        barChartNumPagos.getData().add(serieNumPagos);
        barChartPrecioMedio.getData().add(seriePrecioMedio);
    }

    // Aspectos estéticos
    private void personalizarDiagramas() {
        // Título
        barChartBeneficioMedio.setTitle("Media de beneficio en pagos");
        barChartPartMedias.setTitle("Media de participaciones por participación en pagos");
        barChartNumPagos.setTitle("Número de pagos en el último mes");
        barChartPrecioMedio.setTitle("Precio medio en ventas de participaciones este mes");

        // Ejes
        ejeXBeneficioMedio.setLabel("Empresas");
        ejeXPartMedias.setLabel("Empresas");
        ejeXNumPagos.setLabel("Empresas");
        ejeXPrecioMedio.setLabel("Empresas");
        barChartBeneficioMedio.getYAxis().setLabel("Beneficio medio");
        barChartPartMedias.getYAxis().setLabel("Participaciones medias");
        barChartNumPagos.getYAxis().setLabel("Número de pagos");
        barChartPrecioMedio.getYAxis().setLabel("Precio medio");

        // Rotar etiquetas
        ejeXBeneficioMedio.setTickLabelRotation(50);
        ejeXPartMedias.setTickLabelRotation(50);
        ejeXNumPagos.setTickLabelRotation(50);
        ejeXPrecioMedio.setTickLabelRotation(50);

        // No mostrar leyenda
        barChartBeneficioMedio.setLegendVisible(false);
        barChartPartMedias.setLegendVisible(false);
        barChartNumPagos.setLegendVisible(false);
        barChartPrecioMedio.setLegendVisible(false);

        // Colores
        barChartBeneficioMedio.getData().get(0).getData().forEach(item ->
                item.getNode().setStyle("-fx-background-color: #4059a9"));
        barChartPartMedias.getData().get(0).getData().forEach(item ->
                item.getNode().setStyle("-fx-background-color: #4059a9"));
        barChartNumPagos.getData().get(0).getData().forEach(item ->
                item.getNode().setStyle("-fx-background-color: #4059a9"));
        barChartPrecioMedio.getData().get(0).getData().forEach(item ->
                item.getNode().setStyle("-fx-background-color: #4059a9"));
    }

    // On click en el botón actualizar. Se actualizan los datos de las estadísticas
    public void refrescar() {
        super.getDAO(EstadisticasDAO.class).refrescarEstadisticas();

        // Se reconstruyen los diagramas
        estadisticas.clear();
        barChartBeneficioMedio.getData().clear();
        barChartPartMedias.getData().clear();
        barChartNumPagos.getData().clear();
        barChartPrecioMedio.getData().clear();
        ejeXBeneficioMedio.getCategories().clear();
        ejeXPartMedias.getCategories().clear();
        ejeXNumPagos.getCategories().clear();
        ejeXPrecioMedio.getCategories().clear();
        construirDatos();
        construirEjesX();
        construirEjesY();
        personalizarDiagramas();
    }

    // Se vuelve a la ventana principal
    public void onClickVolver() {
        Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
    }
}
