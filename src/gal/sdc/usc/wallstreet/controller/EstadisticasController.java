package gal.sdc.usc.wallstreet.controller;

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
    public static final Integer HEIGHT = 450;
    public static final Integer WIDTH = 600;
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
    private BarChart<String, Float> barChartNumPagos;
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
    public void initialize(){
        // Obtener datos estadísticos sobre las empresas
        construirDatos();
        // Se indican los valores del eje X
        construirEjesX();
        // Valores eje Y
        construirEjesY();
    }

    private void construirDatos(){
        // Se obtienen todos los datos de la materialized view
        estadisticas.addAll(super.getDAO(EstadisticasDAO.class).getDatos());

        // Para cada diagrama de barras, se toman las 5 empresas más destacadas
        estadBeneficioMedio = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getBeneficioMedio))
                .filter(estad -> Objects.nonNull(estad.getBeneficioMedio()))
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
        estadPartMedias = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getParticipacionesMedias))
                .filter(estad -> Objects.nonNull(estad.getParticipacionesMedias()))
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
        estadNumPagos = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getNumPagosMes))
                .filter(estad -> Objects.nonNull(estad.getNumPagosMes()))
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
        estadPrecioMedio = estadisticas.stream()
                .sorted(Comparator.comparing(Estadistica::getPrecioMedioMes))
                .filter(estad -> Objects.nonNull(estad.getPrecioMedioMes()))
                .limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void construirEjesX(){
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

    private void construirEjesY(){
        XYChart.Series<String, Float> serieBenMedio = new XYChart.Series<>();
        XYChart.Series<String, Float> serieNumPagos = new XYChart.Series<>();
        XYChart.Series<String, Float> seriePartMedias = new XYChart.Series<>();
        XYChart.Series<String, Float> seriePrecioMedio = new XYChart.Series<>();

        // Se construye un XYChart.Data para cada empresa de cada diagrama y es añade a su serie
        for (Estadistica estad : estadBeneficioMedio){
            serieBenMedio.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getBeneficioMedio()));
        }
        for (Estadistica estad : estadPartMedias){
            seriePartMedias.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getParticipacionesMedias()));
        }
        for (Estadistica estad : estadNumPagos){
            serieBenMedio.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getNumPagosMes()));
        }
        for (Estadistica estad : estadPrecioMedio){
            seriePartMedias.getData().add(new XYChart.Data<>(estad.getIdentificadorEmpresa(), estad.getPrecioMedioMes()));
        }

        // Se conectan los datos a los diagramas
        barChartBeneficioMedio.getData().add(serieBenMedio);
        barChartPartMedias.getData().add(seriePartMedias);
        barChartNumPagos.getData().add(serieNumPagos);
        barChartPrecioMedio.getData().add(seriePrecioMedio);
    }
}
