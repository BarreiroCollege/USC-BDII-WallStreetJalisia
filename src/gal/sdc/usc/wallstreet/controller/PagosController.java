package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.*;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.PagoDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public class PagosController extends DatabaseLinker {
    public static final String VIEW = "pagos";
    public static final Integer HEIGHT = 555;
    public static final Integer WIDTH = 867;
    public static final String TITULO = "Ventana pagos participaciones";

    @FXML
    private JFXButton buttonVolverPagosProgramados;

    @FXML
    private JFXButton buttonVolverPagos;

    @FXML
    private JFXButton buttonBuscar;

    @FXML
    private Pane paneFiltro;

    @FXML
    private JFXToggleButton toggleFiltrado;

    @FXML
    private TableView <Pago> tablaPagosProgramados;

    @FXML
    private TableColumn<Pago, String> colBeneficio;

    @FXML
    private TableColumn<Pago, String> colFechaAnuncio;

    @FXML
    private TableColumn<Pago, String> colFechaPago;

    @FXML
    private TableColumn<Pago, String> colParticipacion;

    @FXML
    private JFXDatePicker dPagoDespuesDe;

    @FXML
    private JFXDatePicker dPagoAntesDe;

    @FXML
    private JFXDatePicker dAnuncioPagoDespuesDe;

    @FXML
    private JFXDatePicker dAnuncioPagoAntesDe;

    @FXML
    private JFXComboBox cbMetodoPago;

    @FXML
    private JFXTextField txtDinero;

    @FXML
    private JFXTextField txtParticipaciones;

    @FXML
    private JFXCheckBox cbPagoProgramado;

    @FXML
    private JFXDatePicker dFechaPago;

    @FXML
    private Spinner<Double> sPorcentajeBeneficios;

    @FXML
    private Spinner<Double> sPorcentajeParticipaciones;

    private final ObservableList<Pago> datosTablaPagos = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        
        toggleFiltrado.setSelected(true);
        togglePanelFiltro();
        establecerColumnasTablas();
        tablaPagosProgramados.setPlaceholder(new Label("No existen pagos realizados"));

        dFechaPago.setDisable(true);
        actualizarDatos();
        filtrarDatosPagos();
        cbMetodoPago.getItems().addAll("Dinero", "Participaciones", "Ambas");
        cbMetodoPago.setValue("Dinero");
        txtDinero.setDisable(false);
        txtParticipaciones.setDisable(true);

        buttonVolverPagosProgramados.setOnAction(event -> {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        });

        buttonVolverPagos.setOnAction(event -> {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        });

        buttonBuscar.setOnAction(event -> {
            filtrarDatosPagos();
        });

        toggleFiltrado.setOnAction(event -> {
            togglePanelFiltro();
        });

        cbMetodoPago.setOnAction(event -> {
            if(cbMetodoPago.getValue().equals("Dinero")){
                txtDinero.setDisable(false);
                txtParticipaciones.setDisable(true);
            } else if(cbMetodoPago.getValue().equals("Participaciones")) {
                txtDinero.setDisable(true);
                txtParticipaciones.setDisable(false);
            } else if(cbMetodoPago.getValue().equals("Ambas")){
                txtDinero.setDisable(false);
                txtParticipaciones.setDisable(false);
            }
        });

        cbPagoProgramado.setOnAction(event -> {
            if(cbPagoProgramado.isSelected()){
                dFechaPago.setDisable(false);
            } else{
                dFechaPago.setDisable(true);
            }
        });

    }

    public void actualizarDatos(){
        Usuario usuario = super.getUsuarioSesion().getUsuario();

        // Accedemos a los DAOs para obtener los datos del usuario actual
        List<Pago> pagos = super.getDAO(PagoDAO.class).getPagos(usuario);

        datosTablaPagos.setAll(pagos);
    }

    public void filtrarDatosPagos(){
        tablaPagosProgramados.setPlaceholder(new Label("No se encuentran pagos con los parámetros indicados"));

        FilteredList<Pago> pagosFiltrados = new FilteredList<>(datosTablaPagos, p -> true);

        // Se eliminan aquellas participaciones no válidas
        Predicate<Pago> predicadoTotal = construirPredicadosFiltroPagos();
        pagosFiltrados.setPredicate(predicadoTotal);

        // Una FilteredList no se puede modificar. Se almacena como SortedList para que pueda ser ordenada.
        SortedList<Pago> pagosOrdenados = new SortedList<>(pagosFiltrados);

        // La ordenación de partOrdenadas sigue el criterio de la tabla.
        pagosOrdenados.comparatorProperty().bind(tablaPagosProgramados.comparatorProperty());

        // Se borra la antigua información de la tabla y se muestra la nueva.
        tablaPagosProgramados.setItems(pagosOrdenados);
    }

    private Predicate<Pago> construirPredicadosFiltroPagos() {
        //Predicado correspondiente al beneficiario
        Predicate<Pago> predAntesFecha = pago -> {
            if (dPagoAntesDe.getValue() != null && !dPagoAntesDe.getValue().toString().isEmpty()) {
                if (pago.getFecha() == null){
                    return false;
                }
                return pago.getFecha()
                        .compareTo(java.sql.Date.valueOf(dPagoAntesDe.getValue())) <= 0;
            }
            return true;
        };

        Predicate<Pago> predDespuesFecha = pago -> {
            if (dPagoDespuesDe.getValue() != null && !dPagoDespuesDe.getValue().toString().isEmpty()) {
                if (pago.getFecha() == null){
                    return false;
                }
                return pago.getFecha()
                        .compareTo(java.sql.Date.valueOf(dPagoDespuesDe.getValue())) >= 0;
            }
            return true;
        };

        Predicate<Pago> predAnuncioAntesFecha = pago -> {
            if (dAnuncioPagoAntesDe.getValue() != null && !dAnuncioPagoAntesDe.getValue().toString().isEmpty()) {
                if (pago.getFechaAnuncio() == null){
                    return false;
                }
                return pago.getFechaAnuncio()
                        .compareTo(java.sql.Date.valueOf(dAnuncioPagoAntesDe.getValue())) <= 0;
            }
            return true;
        };

        Predicate<Pago> predAnuncioDespuesFecha = pago -> {
            if (dAnuncioPagoDespuesDe.getValue() != null && !dAnuncioPagoDespuesDe.getValue().toString().isEmpty()) {
                if (pago.getFechaAnuncio() == null){
                    return false;
                }
                return pago.getFechaAnuncio()
                        .compareTo(java.sql.Date.valueOf(dAnuncioPagoDespuesDe.getValue())) >= 0;
            }
            return true;
        };
        return predAntesFecha.and(predDespuesFecha).and(predAnuncioAntesFecha).and(predAnuncioDespuesFecha);
    }


   void establecerColumnasTablas(){
        final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/L/y"); // Asigna el formato de fecha para la tabla de participaciones
        final DateFormat formatoFechaTabla = new SimpleDateFormat("d/L/y");   // Asigna el formato de fecha para la tabla de ofertas de venta

        colFechaPago.setCellValueFactory(celda -> new SimpleStringProperty(formatoFechaTabla.format(celda.getValue().getFecha())));
        colFechaPago.setStyle("-fx-alignment: CENTER;");
        colFechaAnuncio.setCellValueFactory(celda -> new SimpleStringProperty(formatoFechaTabla.format(celda.getValue().getFechaAnuncio())));
        colFechaAnuncio.setStyle("-fx-alignment: CENTER;");
        colBeneficio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>(){
           @Override
           public ObservableValue<String> call(TableColumn.CellDataFeatures<Pago, String> param) {
               if(param.getValue() != null){
                   Float beneficio = param.getValue().getBeneficioPorParticipacion();
                   beneficio *= 100;
                   return new SimpleStringProperty(beneficio.toString() + " %");
               } else{
                   return new SimpleStringProperty("<sin especificar>");
               }
           }
       });
        colBeneficio.setStyle("-fx-alignment: CENTER;");
        colParticipacion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Pago, String> param) {
                if(param.getValue() != null){
                    Float participacion = param.getValue().getParticipacionPorParticipacion();
                    participacion *= 100;
                    return new SimpleStringProperty(participacion.toString() + " %");
                } else{
                    return new SimpleStringProperty("<sin especificar>");
                }
            }
        });
        colParticipacion.setStyle("-fx-alignment: CENTER;");
    }

    public void togglePanelFiltro() {
        if (toggleFiltrado.isSelected()) {
            paneFiltro.setVisible(true);
            tablaPagosProgramados.setPrefSize(425.0, 370.0);
            colParticipacion.setPrefWidth(90.0);
            colBeneficio.setPrefWidth(60.0);
            colFechaPago.setPrefWidth(137.5);
            colFechaAnuncio.setPrefWidth(137.5);

        } else {
            paneFiltro.setVisible(false);
            tablaPagosProgramados.setPrefSize(700, 370);
            colFechaAnuncio.setPrefWidth(175.0);
            colFechaPago.setPrefWidth(175.0);
            colBeneficio.setPrefWidth(175.0);
            colParticipacion.setPrefWidth(175.0);
        }
    }

    public boolean regexPrecio(JFXTextField entrada){
        // La entrada acepta uno o más números, que pueden ir seguidos de un punto y hasta 2 números decimales.
        if (entrada.getText() != null && !entrada.getText().isEmpty()) {
            if (!entrada.getText().matches("[0-9]+([.][0-9]{1,2})?")) {
                if (txtDinero.getText() == null) Main.mensaje("Introduce un precio válido", 3);
                return false;
            }
        }
        return true;
    }

    public boolean regexNumerico(JFXTextField entrada) {
        // Solo se aceptan caracteres numéricos
        if (entrada.getText() != null && !entrada.getText().isEmpty()) {
            if (!entrada.getText().matches("[0-9]+")) {
                if (txtParticipaciones.getText() == null) Main.mensaje("Introduce un número válido de participaciones", 3);
                return false;
            }
        }
        return true;
    }

    public boolean validarCampos(){
        if(cbMetodoPago.getValue().equals("Dinero")){
            if(!regexPrecio(txtDinero)){
                return false;
            }
            txtParticipaciones.setDisable(true);
        } else if(cbMetodoPago.getValue().equals("Participaciones")){
            if(!regexNumerico(txtParticipaciones)){
                return false;
            }
            txtDinero.setDisable(true);
        } else if(cbMetodoPago.getValue().equals("Ambas")){
            if(!regexPrecio(txtDinero) || !regexNumerico(txtParticipaciones)){
                return false;
            }
        }
        dFechaPago.setDisable(!cbPagoProgramado.isSelected());
        return !cbPagoProgramado.isDisabled() || cbPagoProgramado.getText() != null;
    }

    public void insertarPago(){
        if(!validarCampos()){
            return;
        }

    }

}
