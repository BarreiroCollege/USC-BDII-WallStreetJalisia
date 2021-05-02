package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.IntegerValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.*;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReguladorController extends DatabaseLinker {
    public static final String VIEW = "regulador";
    public static final Integer HEIGHT = 500;
    public static final Integer WIDTH = 700;
    public static final String TITULO = "Administración";


    //<editor-fold defaultstate="collapsed" desc="Variables desde FXML">
    // Pestaña de pendientes
    // Número de pendientes
    @FXML
    private Label txtSolicitudesRegistro;
    @FXML
    private Label txtSolicitudesBaja;
    @FXML
    private Label txtSolicitudesOferta;

    // Botones para la apertura de ventanas
    @FXML
    private JFXButton btnVerRegistros;
    @FXML
    private JFXButton btnVerBajas;
    @FXML
    private JFXButton btnVerOfertas;

    // Botones de gestión automática
    @FXML
    private JFXButton btnAceptarTodoRegistros;
    @FXML
    private JFXButton btnAceptarTodoBajas;
    @FXML
    private JFXButton btnAceptarTodoOfertas;

    @FXML
    private Label txtSaldo;
    @FXML
    private JFXButton btnCerrarSesion;


    // Pestaña de transferencias
    // Tabla de usuarios (dos columnas: identificador y saldo)
    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, String> columnaId;
    @FXML
    private TableColumn<Usuario, String> columnaSaldo;

    // Botones para indicar la transferencia
    @FXML
    private JFXButton btnTransferir;
    @FXML
    private JFXButton btnParaTabla;
    @FXML
    private JFXButton btnDeTabla;

    // Selectores
    @FXML
    private JFXTextField txtCampoDe;
    @FXML
    private JFXTextField txtCampoPara;
    @FXML
    private ChoiceBox<String> cbTipo;
    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtDniCif;
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextField txtApellidos;
    @FXML
    private JFXTextField txtCantidad;

    // Pestaña de pagos pendientes
    @FXML
    private TableView<Pago> tablaPagos;
    @FXML
    private TableColumn<Pago, String> columnaEmpresa;
    @FXML
    private TableColumn<Pago, String> columnaAnuncio;
    @FXML
    private TableColumn<Pago, String> columnaPago;
    @FXML
    private TableColumn<Pago, Double> columnaBeneficio;
    @FXML
    private TableColumn<Pago, Double> columnaParticipaciones;

    @FXML
    private JFXButton btnEliminarPago;
    @FXML
    private JFXComboBox<String> cbEmpresa;
    @FXML
    private JFXDatePicker datePagoAntes;
    @FXML
    private JFXDatePicker datePagoDespues;

    //</editor-fold>

    private final String error = "error";       // Mensaje de error
    // Datos a mostrar en la tabla
    private final ObservableList<Usuario> datosTabla = FXCollections.observableArrayList();
    private final ObservableList<Pago> datosTablaPagos = FXCollections.observableArrayList();

    private Usuario campoDe;            // Usuario que transfiere (null indica un agente externo)
    private Usuario campoPara;          // Usuario que recibe transferencia (null indica que se retira saldo)

    private FilteredList<String> empresas; // Lista donde se aplican filtros a las empresas

    /*
     * Si el regulador pulsa "aceptar tod0", se debería ejecutar la acción solo sobre aquellas tuplas de las que tenga
     * constancia. Ejemplo: puede que se esté mostrando que únicamente hay 2 ofertas de venta pendientes, pero que, en
     * el intervalo de tiempo entre que se muestra esa información y el regulador pulsa aceptar, lleguen 100 ofertas
     * de venta. En ese caso, podría ocurrir que el regulador quisiera revisarlas una a una al ser un número tan
     * elevado. Por tanto, al pulsar "aceptar tod_o", solo se deberían actualizar las 2 tuplas originales.
     */
    private List<OfertaVenta> ofertasPendientes;
    private List<Usuario> usuariosRegistroPendientes;
    private List<Usuario> usuariosBajasPendientes;


    /** No existe un botón de actualización de la tabla porque filtrar también refresca los datos **/

    @FXML
    public void initialize() {
        actualizarDatosPendientes();        // Se indica cuántos registros, bajas y ofertas de venta hay pendientes
        // Dependiendo de esas cantidades, se mostrarán unos botones u otros

        setupComponentes();
        addValidadores();
        registrarDatosTabla();              // Se busca a los usuarios activos, que pueden realizar transferencias
        establecerColumnasTabla();          // Se establece la estructura de la tabla
        updateListaPagos();                 // Actualizamos la lista de pagos en el sistema
        setupListeners();
    }

    public void actualizarDatosPendientes() {
        actualizarRegistrosPendientes();
        actualizarBajasPendientes();
        actualizarOfertasPendientes();
        actualizarSaldo();
    }

    public void actualizarRegistrosPendientes() {
        // Usuarios que han solicitado registrarse y están pendientes de ser revisados
        usuariosRegistroPendientes = super.getDAO(UsuarioDAO.class).getInactivos();

        // Se muestra dicha información si no ha habido un error
        txtSolicitudesRegistro.setText(usuariosRegistroPendientes == null? error : String.valueOf(usuariosRegistroPendientes.size()));
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay registros pendientes
        btnVerRegistros.setVisible(usuariosRegistroPendientes != null && usuariosRegistroPendientes.size() != 0);
        btnAceptarTodoRegistros.setVisible(usuariosRegistroPendientes != null && usuariosRegistroPendientes.size() != 0);
    }

    public void actualizarBajasPendientes() {
        // Usuarios que han solicitado darse de baja y están pendientes de ser revisados
        usuariosBajasPendientes = super.getDAO(UsuarioDAO.class).getPendientesBaja();

        // Se muestra dicha información si no ha habido un error
        txtSolicitudesBaja.setText(usuariosBajasPendientes == null ? error : String.valueOf(usuariosBajasPendientes.size()));
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay bajas pendientes
        btnVerBajas.setVisible(usuariosBajasPendientes != null && usuariosBajasPendientes.size() != 0);
        btnAceptarTodoBajas.setVisible(usuariosBajasPendientes != null && usuariosBajasPendientes.size() != 0);
    }

    public void actualizarOfertasPendientes() {
        // Ofertas de venta que no han sido aprobadas
        ofertasPendientes = super.getDAO(OfertaVentaDAO.class).getOfertasPendientes();

        // Se muestra dicha información si no ha habido un error
        txtSolicitudesOferta.setText(ofertasPendientes == null? error : String.valueOf(ofertasPendientes.size()));
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay ofertas pendientes
        btnVerOfertas.setVisible(ofertasPendientes != null && ofertasPendientes.size() != 0);
        btnAceptarTodoOfertas.setVisible(ofertasPendientes != null && ofertasPendientes.size() != 0);
    }

    public void actualizarSaldo() {
        txtSaldo.setText(super.getDAO(ReguladorDAO.class).getDatoRegulador("saldo"));
    }

    public void registrarDatosTabla(){
        // Inicialmente solo se muestran los 100 usuarios de más saldo (filtrar actualizará la tabla)
        List<Usuario> usuarios = super.getDAO(UsuarioDAO.class).getUsuariosMasSaldo(100, super.getDAO(ReguladorDAO.class));
        datosTabla.setAll(usuarios);
        tablaUsuarios.setItems(datosTabla);
    }

    public void updateListaPagos(){
        List<Pago> pagos = super.getDAO(PagoDAO.class).getPagosProgramados();
        datosTablaPagos.setAll(pagos);
        tablaPagos.setItems(datosTablaPagos);

        datosTablaPagos.forEach(pago -> {
            if (!cbEmpresa.getItems().contains(pago.getEmpresa().getNombre() ))
                cbEmpresa.getItems().add(pago.getEmpresa().getNombre() );
        });

        empresas = cbEmpresa.getItems().filtered(null);
    }

    public void setupComponentes(){
        // No hay seleccionada ninguna columna de la tabla
        btnDeTabla.setVisible(false);
        btnParaTabla.setVisible(false);
        btnTransferir.setVisible(false);

        // Icon en el menú item
        Image iconoCerrarSesion = new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/sign_out.png"));
        ImageView menuIcon = new javafx.scene.image.ImageView(iconoCerrarSesion);
        menuIcon.setOpacity(0.5);
        menuIcon.setFitHeight(25);
        menuIcon.setFitWidth(40);
        btnCerrarSesion.setGraphic(menuIcon);

        // Opciones ComboBox
        List<String> opcionesComboBox = new ArrayList<>(Arrays.asList("---", "Empresas", "Inversores"));
        cbTipo.setItems(FXCollections.observableArrayList(opcionesComboBox));
        // Aún no se ha seleccionado ninguna opción
        txtDniCif.setVisible(false);
        txtNombre.setVisible(false);
        txtApellidos.setVisible(false);
    }


    public void addValidadores(){
        // Validador de entrada numérica
        IntegerValidator iv = new IntegerValidator("Valor no numérico");
        txtCantidad.getValidators().add(iv);

        // Si lo introducido contiene caracteres no numéricos, aparece un error y no se muestra el botón de transferir
        txtCantidad.textProperty().addListener((observable, oldValue, newValue) -> {
            if (txtCantidad.getText() != null && !txtCantidad.getText().isEmpty() && txtCantidad.validate()) {
                determinarActivacionBtnTransferencia();
            } else {
                btnTransferir.setVisible(false);
            }
        });
    }

    public void establecerColumnasTabla() {
        // Establecemos los valores que contendrá cada columna de la tabla de participaciones
        columnaId.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getSuperUsuario().getIdentificador()));
        columnaSaldo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getSaldo().toString()));

        // Establecemos los valores que contendrá cada columna de la tabla de pagos
        final DateFormat formatoFecha = new SimpleDateFormat("d/L/y");
        tablaPagos.setPlaceholder(new Label("No existen pagos programados actualmente"));
        columnaEmpresa.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getNombre()));
        columnaAnuncio.setCellValueFactory(celda -> new SimpleStringProperty( celda.getValue().getFechaAnuncio() == null ? "Sin anunciar" : formatoFecha.format(celda.getValue().getFechaAnuncio()) ));
        columnaPago.setCellValueFactory(celda -> new SimpleStringProperty( formatoFecha.format(celda.getValue().getFecha()) ));
        columnaBeneficio.setCellValueFactory(new PropertyValueFactory<>("porcentajeBeneficio"));
        columnaParticipaciones.setCellValueFactory(new PropertyValueFactory<>("porcentajeParticipacion"));
    }

    /**
     * Se aceptan los registros pendientes.
     */
    public void aceptarTodoRegistros() {
        // Dado que un usuario no puede realizar ninguna acción hasta ser aceptada su solicitud, no va a haber
        // conflictos, y se puede utilizar un nivel de lecturas no comprometidas para acelerar la ejecución.
        // No tenemos problemas de cara a admitir usuarios de los que el regulador no tenía constancia al pasar
        // la lista usuariosRegistroPendientes.
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(UsuarioDAO.class).aceptarUsuariosTodos(usuariosRegistroPendientes);
        if (super.ejecutarTransaccion()) Main.mensaje("Solicitudes aceptadas correctamente", 3);
        else Main.mensaje("Se ha producido un error", 3);
        actualizarRegistrosPendientes();
    }

    /**
     * Se aceptan todas las solicitudes de baja (comprobando que se puedan realizar)
     */
    public void aceptarTodoBajas(){
        boolean hayRechazos = false;        // Aviso al finalizar

        /***
         * Para evitar que se produzcan movimientos en la cuenta del usuario una vez el regulador ha aceptado la baja
         * (pues el usuario aún podría comprar participaciones), se fuerza a una ejecución secuencial.
         */
        super.iniciarTransaccion(Connection.TRANSACTION_SERIALIZABLE);

        // Se recogen todos los identificadores de los usuarios a dar de baja (ya buscados)
        List<String> identificadores = usuariosBajasPendientes.stream().map(
                usuario -> usuario.getSuperUsuario().getIdentificador()
        ).collect(Collectors.toList());

        // Iterador para poder quitar elementos de identificadores mientras se recorre la lista
        Iterator<String> iterator = identificadores.iterator();
        while (iterator.hasNext()){
            String identificador = iterator.next();
            if (super.getDAO(ParticipacionDAO.class).tieneParticipaciones(identificador)){
                // Se rechaza la baja del usuario
                hayRechazos = true;
                super.getDAO(UsuarioDAO.class).rechazarBaja(identificador);
                iterator.remove();
            } else {
                // Se prepara la cuenta transfiriendo todos los fondos
                super.getDAO(UsuarioDAO.class).vaciarSaldo(identificador);
            }
        }

        // Se da de baja a todos aquellos usuarios no rechazados
        super.getDAO(UsuarioDAO.class).darDeBajaUsuarios(identificadores);

        if (super.ejecutarTransaccion()){
            if (hayRechazos) {
                Main.mensaje("Las bajas de cuentas con participaciones se han rechazado.", 3);
            } else {
                Main.mensaje("Todas las bajas de cuentas han sido aceptadas", 3);
            }
        } else  {
            Main.mensaje("Error en la gestión de bajas", 3);
        }
        actualizarBajasPendientes();            // Se actualizan los datos mostrados
    }

    /**
     * Acepta todas las solicitudes de oferta de venta con la comisión estándar (0.05).
     */
    public void aceptarTodoOfertas() {
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(OfertaVentaDAO.class).aceptarOfertasVentaPendientes(ofertasPendientes);
        if (super.ejecutarTransaccion()) Main.mensaje("Ofertas aceptadas correctamente", 3);
        else Main.mensaje("Se ha producido un error", 3);
        actualizarOfertasPendientes();      // Se actualizan los datos
    }

    /**
     * Actualiza los datos de la tabla en función de los filtros indicados (se vuelven a cargar los saldos)
     */
    public void onClickFiltrar(){
        tablaUsuarios.setPlaceholder(new Label("No existen usuarios con los parámetros indicados"));
        List<Usuario> usuariosFiltrados = super.getDAO(UsuarioDAO.class)
                .getUsuariosFiltroPersonalizado(construirDatosFiltro(), 100);
        datosTabla.setAll(usuariosFiltrados);
        tablaUsuarios.setItems(datosTabla);
    }

    private HashMap<String, String> construirDatosFiltro(){
        HashMap<String, String> datosFiltrado = new HashMap<>();

        // id
        if (txtId.getText() != null && !txtId.getText().isEmpty()) datosFiltrado.put("id", txtId.getText());

        // ¿Empresa o inversor?
        if (cbTipo.getValue() != null && !cbTipo.getValue().isEmpty() && !cbTipo.getValue().equals("---"))
            datosFiltrado.put("tipo", cbTipo.getValue());

        // DNI o CIF
        if (txtDniCif.isVisible() && txtDniCif.getText() != null && !txtDniCif.getText().isEmpty()){
            datosFiltrado.put(txtDniCif.getPromptText(), txtDniCif.getText());
        }

        // Nombre
        if (txtNombre.isVisible() && txtNombre.getText() != null && !txtNombre.getText().isEmpty()){
            datosFiltrado.put("nombre", txtNombre.getText());
        }

        // Apellidos
        if (txtApellidos.isVisible() && txtApellidos.getText() != null && !txtApellidos.getText().isEmpty()){
            datosFiltrado.put("apellidos", txtApellidos.getText());
        }

        return datosFiltrado;
    }

    private void setupListeners(){
        cbEmpresa.valueProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<String> empresasFiltradas = empresas;
            empresasFiltradas.setPredicate(empresa -> {
                if (newValue == null || newValue.isEmpty()) {
                    // Corrige bug cuando se intentan borrar caracteres
                    return true;
                }
                return empresa.toLowerCase().contains(newValue.toLowerCase());
            });
            cbEmpresa.setItems(empresasFiltradas);
        });
    }
    public void filtrarTablaPagos(){
        tablaPagos.setPlaceholder(new Label("No existen pagos con los parámetros indicados"));

        // Se guardan todos los pagos sin filtrar
        FilteredList<Pago> pagosFiltrados = new FilteredList<>(datosTablaPagos, p -> true);

        // Se eliminan los pagos que no queremos mostrar
        Predicate<Pago> predicadoFiltro = pagoFilterPredicates();
        pagosFiltrados.setPredicate( predicadoFiltro );

        SortedList<Pago> pagosOrdenados = new SortedList<>(pagosFiltrados);
        pagosOrdenados.comparatorProperty().bind(tablaPagos.comparatorProperty());

        // Se borra la antigua información de la tabla y se muestra la nueva.
        tablaPagos.setItems(pagosOrdenados);
    }

    private Predicate<Pago> pagoFilterPredicates() {

        Predicate<Pago> combobox = pago -> {
            if (cbEmpresa.getValue() != null && !cbEmpresa.getValue().isEmpty()) {
                // El nombre comercial de la empresa del pago debe contener la selección
                return pago.getEmpresa().getNombre().toLowerCase().contains(cbEmpresa.getValue().toLowerCase());
            }
            return true;
        };

        Predicate<Pago> fechaAntes = pago -> {
            if ( datePagoAntes.getValue() != null && !datePagoAntes.getValue().toString().isEmpty() ){
                return pago.getFecha().compareTo( java.sql.Date.valueOf( datePagoAntes.getValue() ) ) <= 0;
            }
            return true;
        };

        Predicate<Pago> fechaDespues = pago -> {
            if ( datePagoDespues.getValue() != null && !datePagoDespues.getValue().toString().isEmpty() ){
                return pago.getFecha().compareTo( java.sql.Date.valueOf( datePagoDespues.getValue() ) ) >= 0;
            }
            return true;
        };

        return combobox.and(fechaAntes).and(fechaDespues);
    }

    /**
     * Filtrado por tipo de usuario (varían los campos que se pueden cubrir)
     */
    public void onActionComboBoxTipo(){
        if ("Empresas".equals(cbTipo.getValue())){
            txtDniCif.setVisible(true);
            txtDniCif.setPromptText("CIF");
            txtNombre.setVisible(true);
            txtNombre.setPromptText("Nombre comercial");
            txtApellidos.setVisible(false);
        } else if ("Inversores".equals(cbTipo.getValue())){
            txtDniCif.setVisible(true);
            txtDniCif.setPromptText("DNI");
            txtNombre.setVisible(true);
            txtNombre.setPromptText("Nombre");
            txtApellidos.setVisible(true);
        } else {
            txtDniCif.setVisible(false);
            txtNombre.setVisible(false);
            txtApellidos.setVisible(false);
        }
    }

    /**
     * Realizar una transferencia desde campoDe a campoPara con la cantidad indicada
     */
    public void onClickBtnTransferir(){
        if (campoPara == null){                   // Los fondos se retiran de la cuenta
            // Se comprueba que haya saldo suficiente
            if (Float.parseFloat(txtCantidad.getText()) > campoDe.getSaldo()){
                Main.mensaje("Saldo insuficiente", 5);
                return;
            }

            // Hay saldo suficiente. Se retiran los fondos.
            if (super.getDAO(UsuarioDAO.class).retirarSaldo(Float.parseFloat(txtCantidad.getText()), campoDe)){
                // Se actualiza la tabla y se da un aviso
                Main.mensaje("Transferencia realizada correctamente");
                campoDe.setSaldo(campoDe.getSaldo() - Float.parseFloat(txtCantidad.getText()));
                // Se refresca la tabla.
                tablaUsuarios.getColumns().get(0).setVisible(false);
                tablaUsuarios.getColumns().get(0).setVisible(true);
            } else {
                Main.mensaje("Error en la transferencia");
            }
        } else if (campoDe == null){          // Se depositan fondos
            // Como depositar fondos no supone peligros respecto a comprobaciones, se puede hacer con un nivel de
            // aislamiento de lecturas no comprometidas
            super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
            super.getDAO(UsuarioDAO.class).depositarSaldo(Integer.parseInt(txtCantidad.getText()), campoPara);
            if (super.ejecutarTransaccion()){
                // Se actualiza la tabla y se da un aviso
                Main.mensaje("Transferencia realizada correctamente");
                campoPara.setSaldo(campoPara.getSaldo() + Float.parseFloat(txtCantidad.getText()));
                // Se refresca la tabla
                tablaUsuarios.getColumns().get(0).setVisible(false);
                tablaUsuarios.getColumns().get(0).setVisible(true);
            } else {
                Main.mensaje("Error en la transferencia");
            }
        } else {                                // Transferencia de una cuenta a otra
            // Se comprueba que haya saldo suficiente
            if ((Float.parseFloat(txtCantidad.getText()) > campoDe.getSaldo())){
                Main.mensaje("Saldo insuficiente", 5);
                return;
            }

            super.iniciarTransaccion();
            super.getDAO(UsuarioDAO.class).retirarSaldo(Float.parseFloat(txtCantidad.getText()), campoDe);
            super.getDAO(UsuarioDAO.class).depositarSaldo(Float.parseFloat(txtCantidad.getText()), campoPara);
            if (super.ejecutarTransaccion()){
                // Se actualiza la tabla y se da un aviso
                Main.mensaje("Transferencia realizada correctamente");
                campoDe.setSaldo(campoDe.getSaldo() - Float.parseFloat(txtCantidad.getText()));
                campoPara.setSaldo(campoPara.getSaldo() + Float.parseFloat(txtCantidad.getText()));
                // Se refresca la tabla
                tablaUsuarios.getColumns().get(0).setVisible(false);
                tablaUsuarios.getColumns().get(0).setVisible(true);
            } else {
                Main.mensaje("Error en la transferencia");
            }
        }
    }

    /**
     * Permite escoger el usuario seleccionado en la tabla como un campo para la transferencia.
     */
    public void onClickTabla(){
        btnDeTabla.setVisible(true);
        btnParaTabla.setVisible(true);
    }

    /**
     * Permite el borrado de un pago
     */
    public void onClickTablaPagos(){ btnEliminarPago.setDisable(false); }

    /**
     * Elimina la opción de escoger un dato de la tabla.
     */
    public void onMouseReleasedTabla(){
        btnDeTabla.setVisible(false);
        btnParaTabla.setVisible(false);
    }

    /**
     * Impide el borrado de un pago
     */
    public void onMouseReleasedTablaPagos(){ btnEliminarPago.setDisable(true); }

    public void onClickBtnDeExterior(){
        campoDe = null;
        txtCampoDe.setText("Exterior");
        determinarActivacionBtnTransferencia();
    }

    public void onClickBtnParaExterior(){
        campoPara = null;
        txtCampoPara.setText("Exterior");
        determinarActivacionBtnTransferencia();
    }

    public void onClickBtnDeTabla(){
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        campoDe = usuarioSeleccionado;
        txtCampoDe.setText(usuarioSeleccionado.getSuperUsuario().getIdentificador());
        determinarActivacionBtnTransferencia();
    }

    public void onClickBtnParaTabla(){
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        campoPara = usuarioSeleccionado;
        txtCampoPara.setText(usuarioSeleccionado.getSuperUsuario().getIdentificador());
        determinarActivacionBtnTransferencia();
    }

    /**
     * El botón de transferir solo se muestra si las opciones escogidas son coherentes.
     */
    private void determinarActivacionBtnTransferencia(){
        boolean condicion = true;
        if (campoDe != null){               // Los dos campos no pueden ser iguales
            condicion = !campoDe.equals(campoPara);
        } else if (campoPara == null){      // Ambos campos son nulos
            condicion = false;
        }                                   // Si campoDe == null y campoPara != null, son distintos

        condicion = condicion && txtCantidad.getText() != null && !txtCantidad.getText().isEmpty();

        btnTransferir.setVisible(condicion);
    }

    /**
     * Abre la ventana de revisión de registros
     */
    public void mostrarRevisarRegistros() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/revisarRegistros.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Peticiones de registro");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 600, 400));
            // La ventana del regulador es la ventana padre. Queda visible, pero desactivada.
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAceptarTodoBajas.getScene().getWindow());
            stage.setOnHidden(event -> actualizarRegistrosPendientes());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana de revisión de bajas
     */
    public void mostrarRevisarBajas() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/revisarBajas.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Peticiones de baja");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 600, 400));
            // La ventana del regulador es la ventana padre. Queda visible, pero desactivada.
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAceptarTodoBajas.getScene().getWindow());
            stage.setOnHidden(event -> actualizarBajasPendientes());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana de revisión de ofertas de venta
     */
    public void mostrarRevisarOfertasVenta() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/revisarOfertasVenta.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ofertas de venta no confirmadas");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 550, 350));
            // La ventana del regulador es la ventana padre. Queda visible, pero desactivada.
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAceptarTodoBajas.getScene().getWindow());
            stage.setOnHidden(event -> actualizarOfertasPendientes());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eliminarPago(){
        Pago seleccion = tablaPagos.getSelectionModel().getSelectedItem(); // Leemos el pago seleccionado en la tabla

        super.getDAO(PagoDAO.class).eliminar(seleccion); // Lo eliminamos de la base de datos

        // Y actualizamos la tabla
        updateListaPagos();
        tablaPagos.getColumns().get(0).setVisible(false);
        tablaPagos.getColumns().get(0).setVisible(true);

        btnEliminarPago.setDisable(true); // Desactivamos el boton de borrado, puesto que la fila seleccionada no existe
    }

    public void cerrarSesion(){
        super.cerrarSesion();
        Main.ventana(AccesoController.VIEW, AccesoController.WIDTH, AccesoController.HEIGHT, AccesoController.TITULO);
    }
}
