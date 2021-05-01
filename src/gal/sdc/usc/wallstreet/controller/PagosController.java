package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.*;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.*;
import gal.sdc.usc.wallstreet.repository.PagoDAO;
import gal.sdc.usc.wallstreet.repository.PagoUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.property.SimpleStringProperty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;



import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
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
    private JFXButton buttonPagar;

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
    private Spinner<Integer> sPorcentajeBeneficios;

    @FXML
    private Spinner<Integer> sPorcentajeParticipaciones;

    @FXML
    private ScrollBar scrollVertical;

    private final ObservableList<Pago> datosTablaPagos = FXCollections.observableArrayList();


    @FXML
    public void initialize(){
        establecerColumnasTablas();
        inicializarControles();
        gestionarControles();
        actualizarDatos();
        filtrarDatosPagos();
    }

    public void inicializarControles(){
        toggleFiltrado.setSelected(true);
        togglePanelFiltro();

        tablaPagosProgramados.setPlaceholder(new Label("No existen pagos realizados"));

        dFechaPago.setDisable(true);

        sPorcentajeParticipaciones.setDisable(true);
        sPorcentajeBeneficios.setDisable(true);

        txtDinero.setDisable(false);
        txtParticipaciones.setDisable(true);

        cbMetodoPago.getItems().addAll("Dinero", "Participaciones", "Ambas");
        cbMetodoPago.setValue("Dinero");

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50);
        sPorcentajeBeneficios.setValueFactory(valueFactory);
        sPorcentajeParticipaciones.setValueFactory(valueFactory2);
        sPorcentajeBeneficios.setEditable(true);
        sPorcentajeParticipaciones.setEditable(true);

        tablaPagosProgramados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void gestionarControles(){
        //Control de los Spinners y ComboBox para pagar en Dinero o Participaciones (VENTANA 2)
        cbMetodoPago.setOnAction(event -> {
            if(cbMetodoPago.getValue().equals("Dinero")){
                txtDinero.setDisable(false);
                txtParticipaciones.setDisable(true);
                sPorcentajeParticipaciones.setDisable(true);
                sPorcentajeBeneficios.setDisable(true);
                sPorcentajeBeneficios.setEditable(false);
                sPorcentajeParticipaciones.setEditable(false);
            } else if(cbMetodoPago.getValue().equals("Participaciones")) {
                txtDinero.setDisable(true);
                txtParticipaciones.setDisable(false);
                sPorcentajeParticipaciones.setDisable(true);
                sPorcentajeBeneficios.setDisable(true);
                sPorcentajeBeneficios.setEditable(false);
                sPorcentajeParticipaciones.setEditable(false);
            } else if(cbMetodoPago.getValue().equals("Ambas")){
                txtDinero.setDisable(false);
                txtParticipaciones.setDisable(false);
                sPorcentajeParticipaciones.setDisable(false);
                sPorcentajeBeneficios.setDisable(false);
                sPorcentajeBeneficios.setEditable(true);
                sPorcentajeParticipaciones.setEditable(true);
            }

        });


        //Botón de volver (VENTANA 1)
        buttonVolverPagosProgramados.setOnAction(event2 -> {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        });
        //Botón de volver (VENTANA 2)
        buttonVolverPagos.setOnAction(event3 -> {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        });
        //Botón para filtrar los datos de la tabla (VENTANA 1)
        buttonBuscar.setOnAction(event4 -> {
            filtrarDatosPagos();
        });
        //Botón para insertar un pago (VENTANA 2)
        buttonPagar.setOnAction(event5 -> {
            insertarPago();
            limpiarCampos();
        });
        //Control del CheckBox de pago programado (VENTANA 2)
        cbPagoProgramado.setOnAction(event -> {
            if(cbPagoProgramado.isSelected()){
                dFechaPago.setDisable(false);
            } else{
                dFechaPago.setDisable(true);
            }
        });
        //Botón toggle para ocultar/mostrar las opciones de filtrado (VENTANA 1)
        toggleFiltrado.setOnAction(event -> {
            togglePanelFiltro();
        });


        //Sincronizar valores de spinners
        sPorcentajeBeneficios.valueProperty().addListener((observable, oldValue, newValue) -> {
           sPorcentajeParticipaciones.getValueFactory().setValue(100 - newValue);
        });
        sPorcentajeParticipaciones.valueProperty().addListener((observable, oldValue, newValue) -> {
            sPorcentajeBeneficios.getValueFactory().setValue(100 - newValue);
        });
        sPorcentajeBeneficios.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    sPorcentajeParticipaciones.getValueFactory().setValue(100 - Integer.valueOf(sPorcentajeBeneficios.getEditor().getText()));
                }
            }
        });
        sPorcentajeParticipaciones.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                sPorcentajeBeneficios.getValueFactory().setValue(100 - Integer.valueOf(sPorcentajeParticipaciones.getEditor().getText()));
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
        final DateFormat formatoFechaTabla = new SimpleDateFormat("d/L/y");   // Asigna el formato de fecha para la tabla de ofertas de venta

        colFechaPago.setCellValueFactory(celda -> new SimpleStringProperty(formatoFechaTabla.format(celda.getValue().getFecha())));
        colFechaPago.setStyle("-fx-alignment: CENTER;");
        colFechaAnuncio.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getFechaAnuncio() == null ? "Sin anuncio" : formatoFechaTabla.format(celda.getValue().getFechaAnuncio())));
        colFechaAnuncio.setStyle("-fx-alignment: CENTER;");
        colBeneficio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>(){
           @Override
           public ObservableValue<String> call(TableColumn.CellDataFeatures<Pago, String> param) {
               if(param.getValue() != null){
                   DecimalFormat df = new DecimalFormat("#");
                   Float beneficio = param.getValue().getBeneficioPorParticipacion();
                   Float porcentaje_beneficio = param.getValue().getPorcentajeBeneficio();
                   porcentaje_beneficio *= 100.0f;
                   return new SimpleStringProperty(beneficio.toString() + " x " + df.format(porcentaje_beneficio) + "%");
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
                    DecimalFormat df = new DecimalFormat("#");
                    Float participacion = param.getValue().getParticipacionPorParticipacion();
                    Float porcentaje_participacion = param.getValue().getPorcentajeParticipacion();
                    porcentaje_participacion *= 100.0f;
                    return new SimpleStringProperty(participacion.toString() + " x " + df.format(porcentaje_participacion) + "%");
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
            colBeneficio.setPrefWidth(90.0);
            colFechaPago.setPrefWidth(122.5);
            colFechaAnuncio.setPrefWidth(122.5);

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
                Main.mensaje("El valor del dinero introducido no es válido", 3);
                return false;
            } else if(txtDinero.getText().isEmpty()){
                Main.mensaje("Introduce un valor para la cantidad de dinero", 3);
                return false;
            }
            txtParticipaciones.setDisable(true);
            sPorcentajeParticipaciones.setDisable(true);
            sPorcentajeBeneficios.setDisable(true);
        } else if(cbMetodoPago.getValue().equals("Participaciones")){
            if(!regexNumerico(txtParticipaciones)){
                Main.mensaje("El valor de participaciones introducido no es válido", 3);
                return false;
            } else if(txtParticipaciones.getText().isEmpty()){
                Main.mensaje("Introduce un valor para la cantidad de participaciones");
                return false;
            }
            txtDinero.setDisable(true);
            sPorcentajeParticipaciones.setDisable(true);
            sPorcentajeBeneficios.setDisable(true);
        } else if(cbMetodoPago.getValue().equals("Ambas")){
            if(!regexPrecio(txtDinero)){
                Main.mensaje("El valor del dinero introducido no es válido", 3);
                return false;
            } else if(txtDinero.getText().isEmpty()){
                Main.mensaje("Introduce un valor para la cantidad de dinero", 3);
                return false;
            }
            if(!regexNumerico(txtParticipaciones)){
                Main.mensaje("El valor de participaciones introducido no es válido", 3);
                return false;
            } else if(txtParticipaciones.getText().isEmpty()){
                Main.mensaje("Introduce un valor para la cantidad de participaciones", 3);
                return false;
            }
            sPorcentajeParticipaciones.setDisable(false);
            sPorcentajeBeneficios.setDisable(false);
        }
        if(cbPagoProgramado.isSelected() && dFechaPago.getValue() == null){
            Main.mensaje("Introduce la fecha en la que quieres realizar el pago", 3);
            return false;
        }
        dFechaPago.setDisable(!cbPagoProgramado.isSelected());
        return !cbPagoProgramado.isDisabled() || cbPagoProgramado.getText() != null;
    }

    public boolean insertarPago(){
        Pago p = null;
        List<PagoUsuario> pagosAUsuarios = null;
        if(!validarCampos()){
            return false;
        }
        if(cbPagoProgramado.isSelected()){
            //Construimos pago fecha_anuncio now() y fecha la seleccionada
            switch(cbMetodoPago.getValue().toString()){
                case "Dinero":
                    p = new Pago.Builder()
                            .withFecha(Date.from(dFechaPago.getValue().atStartOfDay().toInstant(ZoneOffset.UTC)))
                            .withEmpresa((Empresa) super.getUsuarioSesion())
                            .withBeneficioPorParticipacion(Float.valueOf(txtDinero.getText()))
                            .withParticipacionPorParticipacion(0.0f)
                            .withFechaAnuncio(new Date())
                            .withPorcentajeBeneficio(1.0f)
                            .withPorcentajeParticipacion(0.0f)
                            .build();
                    break;
                case "Participaciones":
                    p = new Pago.Builder()
                            .withFecha(Date.from(dFechaPago.getValue().atStartOfDay().toInstant(ZoneOffset.UTC)))
                            .withEmpresa((Empresa) super.getUsuarioSesion())
                            .withBeneficioPorParticipacion(0.0f)
                            .withParticipacionPorParticipacion(Float.valueOf(txtParticipaciones.getText()))
                            .withFechaAnuncio(new Date())
                            .withPorcentajeBeneficio(0.0f)
                            .withPorcentajeParticipacion(1.0f)
                            .build();
                    break;
                case "Ambas":
                    p = new Pago.Builder()
                            .withFecha(Date.from(dFechaPago.getValue().atStartOfDay().toInstant(ZoneOffset.UTC)))
                            .withEmpresa((Empresa) super.getUsuarioSesion())
                            .withBeneficioPorParticipacion(Float.valueOf(txtDinero.getText()))
                            .withParticipacionPorParticipacion(Float.valueOf(txtParticipaciones.getText()))
                            .withFechaAnuncio(new Date())
                            .withPorcentajeBeneficio(sPorcentajeBeneficios.getValue() / 100.0f)
                            .withPorcentajeParticipacion((sPorcentajeParticipaciones.getValue() / 100.0f))
                            .build();
                    break;
            }
        }else{
            //Construimos pago fecha_anuncio null y fecha now()
            switch(cbMetodoPago.getValue().toString()){
                case "Dinero":
                    p = new Pago.Builder()
                            .withFecha(new Date())
                            .withEmpresa((Empresa) super.getUsuarioSesion())
                            .withBeneficioPorParticipacion(Float.valueOf(txtDinero.getText()))
                            .withParticipacionPorParticipacion(0.0f)
                            .withFechaAnuncio(null)
                            .withPorcentajeBeneficio(1.0f)
                            .withPorcentajeParticipacion(0.0f)
                            .build();
                    break;
                case "Participaciones":
                    p = new Pago.Builder()
                            .withFecha(new Date())
                            .withEmpresa((Empresa) super.getUsuarioSesion())
                            .withBeneficioPorParticipacion(0.0f)
                            .withParticipacionPorParticipacion(Float.valueOf(txtParticipaciones.getText()))
                            .withFechaAnuncio(null)
                            .withPorcentajeBeneficio(0.0f)
                            .withPorcentajeParticipacion(1.0f)
                            .build();
                    break;
                case "Ambas":
                    p = new Pago.Builder()
                            .withFecha(new Date())
                            .withEmpresa((Empresa) super.getUsuarioSesion())
                            .withBeneficioPorParticipacion(Float.valueOf(txtDinero.getText()))
                            .withParticipacionPorParticipacion(Float.valueOf(txtParticipaciones.getText()))
                            .withFechaAnuncio(null)
                            .withPorcentajeBeneficio(sPorcentajeBeneficios.getValue() / 100.0f)
                            .withPorcentajeParticipacion((sPorcentajeParticipaciones.getValue() / 100.0f))
                            .build();
            }
        }
        if(!super.getDAO(PagoDAO.class).insertarPago(p)){
            return false;
        }
        pagosAUsuarios = obtenerPagosUsuarios(super.getDAO(ParticipacionDAO.class).getParticipacionesPorEmpresa(super.getUsuarioSesion().getUsuario().getSuperUsuario().getIdentificador()) ,p);


        List<Participacion> listaParticipacionUsuarios = super.getDAO(ParticipacionDAO.class).getParticipacionesPorEmpresa(super.getUsuarioSesion().getUsuario().getSuperUsuario().getIdentificador());
        if(super.getDAO(PagoDAO.class).actualizarSaldos(p, txtDinero.getText().isEmpty() ? 0.0f : Float.parseFloat(txtDinero.getText()) * p.getPorcentajeBeneficio(), listaParticipacionUsuarios) && super.getDAO(PagoDAO.class).repartirParticipaciones(p, listaParticipacionUsuarios, txtParticipaciones.getText().isEmpty() ? 0 : Integer.parseInt(txtParticipaciones.getText()) * p.getPorcentajeParticipacion().intValue())){
            Main.mensaje("Se ha realizado el pago con éxito", 3);
        }

            return super.getDAO(PagoUsuarioDAO.class).insertarListaPagos(pagosAUsuarios, super.getUsuarioSesion().getUsuario().getSuperUsuario().getIdentificador());
    }

    public void limpiarCampos(){
        cbMetodoPago.setValue("Dinero");
        txtDinero.clear();
        txtParticipaciones.clear();
        if(dFechaPago.getValue() != null) {
            dFechaPago.getEditor().clear();
        }
        dFechaPago.setDisable(true);
        sPorcentajeParticipaciones.getValueFactory().setValue(50);
        sPorcentajeBeneficios.getValueFactory().setValue(50);
        cbPagoProgramado.setSelected(false);
    }

    public List<PagoUsuario> obtenerPagosUsuarios(List<Participacion> participacionesEmpresa, Pago pa){
        List<PagoUsuario> pagosAUsuarios = new ArrayList<>();
        for (Participacion p: participacionesEmpresa) {
            PagoUsuario pU = new PagoUsuario.Builder().withUsuario(p.getUsuario().getSuperUsuario()).withPago(pa).withNumParticipaciones(p.getCantidad()).build();
            pagosAUsuarios.add(pU);
        }
        System.out.println(pagosAUsuarios);
        return pagosAUsuarios;
    }

}
