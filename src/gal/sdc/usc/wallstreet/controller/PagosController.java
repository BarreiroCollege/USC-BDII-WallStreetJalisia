package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXToggleButton;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.PagoDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class PagosController extends DatabaseLinker {
    public static final String VIEW = "pagos";
    public static final Integer HEIGHT = 555;
    public static final Integer WIDTH = 867;
    public static final String TITULO = "Ventana pagos participaciones";

    @FXML
    private JFXButton buttonVolver;

    @FXML
    private JFXButton buttonBuscar;

    @FXML
    private Pane paneFiltro;

    @FXML
    private JFXToggleButton toggleFiltrado;

    @FXML
    private TableView <Pago> tablaPagosProgramados;

    @FXML
    private TableColumn<Pago, Float> colBeneficio;

    @FXML
    private TableColumn<Pago, Date> colFechaAnuncio;

    @FXML
    private TableColumn<Pago, Date> colFechaPago;

    @FXML
    private TableColumn<Pago, Float> colParticipacion;

    @FXML
    private JFXDatePicker dPDespuesDe;

    @FXML
    private JFXDatePicker dPAntesDe;

    @FXML
    private JFXButton buttonEliminarAnuncioPago;

    private final ObservableList<Pago> datosTablaPagos = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        toggleFiltrado.setSelected(true);
        togglePanelFiltro();
        establecerColumnasTablas();
        tablaPagosProgramados.setPlaceholder(new Label("No existen pagos realizados"));

        actualizarDatos();

        buttonVolver.setOnAction(event -> {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        });

        buttonBuscar.setOnAction(event -> {
            filtrarDatosPagos();
        });

        toggleFiltrado.setOnAction(event -> {
            togglePanelFiltro();
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
            if (dPAntesDe.getValue() != null && !dPAntesDe.getValue().toString().isEmpty()) {
                if (pago.getEmpresa().getFechaUltimoPago() == null){
                    return false;
                }
                return pago.getEmpresa().getFechaUltimoPago()
                        .compareTo(java.sql.Date.valueOf(dPAntesDe.getValue())) <= 0;
            }
            return true;
        };

        Predicate<Pago> predDespuesFecha = pago -> {
            if (dPDespuesDe.getValue() != null && !dPDespuesDe.getValue().toString().isEmpty()) {
                if (pago.getEmpresa().getFechaUltimoPago() == null){
                    return false;
                }
                return pago.getEmpresa().getFechaUltimoPago()
                        .compareTo(java.sql.Date.valueOf(dPDespuesDe.getValue())) >= 0;
            }
            return true;
        };
        return predAntesFecha.and(predDespuesFecha);
    }


   void establecerColumnasTablas(){
        final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/L/y"); // Asigna el formato de fecha para la tabla de participaciones
       final DateFormat formatoFechaTabla = new SimpleDateFormat("d/L/y");   // Asigna el formato de fecha para la tabla de ofertas de venta

        colFechaPago.setCellValueFactory(new PropertyValueFactory<Pago, Date>("fecha"));
        colFechaAnuncio.setCellValueFactory(new PropertyValueFactory<Pago, Date>("fecha_anuncio"));
        colBeneficio.setCellValueFactory(new PropertyValueFactory<Pago, Float>("beneficioPorParticipacion"));
        colParticipacion.setCellValueFactory(new PropertyValueFactory<Pago, Float>("participacionPorParticipacion"));
    }

    public void togglePanelFiltro() {
        if (toggleFiltrado.isSelected()) {
            paneFiltro.setVisible(true);
            tablaPagosProgramados.setPrefSize(425.0, 370.0);
            colParticipacion.setPrefWidth(106.25);
            colBeneficio.setPrefWidth(106.25);
            colFechaPago.setPrefWidth(106.25);
            colFechaAnuncio.setPrefWidth(106.25);

        } else {
            paneFiltro.setVisible(false);
            tablaPagosProgramados.setPrefSize(700, 370);
            colFechaAnuncio.setPrefWidth(175.0);
            colFechaPago.setPrefWidth(175.0);
            colBeneficio.setPrefWidth(175.0);
            colParticipacion.setPrefWidth(175.0);
        }
    }

}
