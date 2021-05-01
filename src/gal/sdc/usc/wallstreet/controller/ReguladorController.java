package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.*;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;
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
    //</editor-fold>

    private final String error = "error";       // Mensaje de error
    // Datos a mostrar en la tabla
    private final ObservableList<Usuario> datosTabla = FXCollections.observableArrayList();

    private Usuario campoDe;            // Usuario que transfiere (null indica un agente externo)
    private Usuario campoPara;          // Usuario que recibe transferencia (null indica que se retira saldo)

    /*
     * Si el regulador pulsa "aceptar tod_o", se debería ejecutar la acción solo sobre aquellas tuplas de las que tenga
     * constancia. Ejemplo: puede que se esté mostrando que únicamente hay 2 ofertas de venta pendientes, pero que, en
     * el intervalo de tiempo entre que se muestra esa información y el regulador pulsa aceptar, lleguen 100 ofertas
     * de venta. En ese caso, podría ocurrir que el regulador quisiera revisarlas una a una al ser un número tan
     * elevado. Por tanto, al pulsar "aceptar tod_o", solo se deberían actualizar las 2 tuplas originales.
     */
    private List<OfertaVenta> ofertasPendientes;
    private List<Usuario> usuariosRegistroPendientes;
    // No se hace lo mismo para las bajas porque el proceso requiere pasos intermedios que ya aseguran esto


    /** No existe un botón de actualización de la tabla porque filtrar también refresca los datos **/

    @FXML
    public void initialize() {
        actualizarDatosPendientes();        // Se indica cuántos registros, bajas y ofertas de venta hay pendientes
        // Dependiendo de esas cantidades, se mostrarán unos botones u otros

        setupComponentes();
        addValidadores();
        registrarDatosTabla();              // Se busca a los usuarios activos, que pueden realizar transferencias
        establecerColumnasTabla();          // Se establece la estructura de la tabla
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
        int numUsuariosPendientes = usuariosRegistroPendientes.size();

        // Se muestra dicha información si no ha habido un error
        txtSolicitudesRegistro.setText(numUsuariosPendientes == 0? error : String.valueOf(numUsuariosPendientes));
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay registros pendientes
        btnVerRegistros.setVisible(numUsuariosPendientes != 0);
        btnAceptarTodoRegistros.setVisible(numUsuariosPendientes != 0);
    }

    public void actualizarBajasPendientes() {
        // Número de usuarios que han solicitado darse de baja y están pendientes de ser revisados
        Integer bajasPendientes = super.getDAO(UsuarioDAO.class).getNumSolicitudesBaja();
        // Se muestra dicha información si no ha habido un error
        txtSolicitudesBaja.setText(bajasPendientes == null ? error : bajasPendientes.toString());
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay bajas pendientes
        btnVerBajas.setVisible(bajasPendientes != null && !bajasPendientes.equals(0));
        btnAceptarTodoBajas.setVisible(bajasPendientes != null && !bajasPendientes.equals(0));
    }

    public void actualizarOfertasPendientes() {
        // Ofertas de venta que no han sido aprobadas
        ofertasPendientes = super.getDAO(OfertaVentaDAO.class).getOfertasPendientes();
        int numOfertasPendientes = ofertasPendientes.size();

        // Se muestra dicha información si no ha habido un error
        txtSolicitudesOferta.setText(numOfertasPendientes == 0 ? error : String.valueOf(numOfertasPendientes));
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay ofertas pendientes
        btnVerOfertas.setVisible(numOfertasPendientes != 0);
        btnAceptarTodoOfertas.setVisible(numOfertasPendientes != 0);
    }

    public void actualizarSaldo() {

    }

    public void registrarDatosTabla(){
        // Inicialmente solo se muestran los 100 usuarios de más saldo (filtrar actualizará la tabla)
        List<Usuario> usuarios = super.getDAO(UsuarioDAO.class).getUsuariosMasSaldo(100);
        datosTabla.setAll(usuarios);
        tablaUsuarios.setItems(datosTabla);
    }

    public void setupComponentes(){
        // No hay seleccionada ninguna columna de la tabla
        btnDeTabla.setVisible(false);
        btnParaTabla.setVisible(false);
        btnTransferir.setVisible(false);

        // Opciones ComboBox
        List<String> opcionesComboBox = new ArrayList<>(Arrays.asList("---", "Empresas", "Inversores"));
        cbTipo.setItems(FXCollections.observableArrayList(opcionesComboBox));
        // Aún no se ha seleccionado ninguna opción
        txtDniCif.setVisible(false);
        txtNombre.setVisible(false);
        txtApellidos.setVisible(false);
    }


    public void addValidadores(){
        // Validadores de entrada numérica
        IntegerValidator iv = new IntegerValidator("");
        txtCantidad.getValidators().add(iv);

        txtCantidad.textProperty().addListener((observable, oldValue, newValue) -> {
            txtCantidad.validate();
            determinarActivacionBtnTransferencia();
        });
        //TODO: fallo
    }

    public void establecerColumnasTabla() {
        // Establecemos los valores que contendrá cada columna de la tabla de participaciones
        columnaId.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getSuperUsuario().getIdentificador()));
        columnaSaldo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getSaldo().toString()));
    }



    /**
     * Se aceptan los registros pendientes.
     * No se admite read uncomitted para evitar que se acepten solicitudes de las que el regulador no tenía constancia.
     */
    public void aceptarTodoRegistros() {
        super.getDAO(UsuarioDAO.class).aceptarUsuariosTodos(usuariosRegistroPendientes);
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

        // Se recogen todos los identificadores de los usuarios a dar de baja
        List<String> identificadores = super.getDAO(EmpresaDAO.class).getEmpresasBajasPendientes().stream().map(
                empresa -> empresa.getUsuario().getSuperUsuario().getIdentificador()
        ).collect(Collectors.toList());
        identificadores.addAll(super.getDAO(InversorDAO.class).getInversoresBajasPendientes().stream().map(
                inversor -> inversor.getUsuario().getSuperUsuario().getIdentificador()
        ).collect(Collectors.toList()));

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
                Main.mensaje("Las bajas de cuentas con participaciones se han rechazado.", 5);
            } else {
                Main.mensaje("Todas las bajas de cuentas han sido aceptadas", 5);
            }
        } else  {
            Main.mensaje("Error en la gestión de bajas", 5);
        }
        actualizarBajasPendientes();            // Se actualizan los datos mostrados
    }

    /**
     * Acepta todas las solicitudes de oferta de venta con la comisión estándar (0.05).
     */
    public void aceptarTodoOfertas() {
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(OfertaVentaDAO.class).aceptarOfertasVentaPendientes(ofertasPendientes);
        super.ejecutarTransaccion();
        actualizarOfertasPendientes();      // Se actualizan los datos
    }

    /**
     * Actualiza los datos de la tabla en función de los filtros indicados (se vuelven a cargar los saldos)
     */
    public void onClickFiltrar(){
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
    // TODO: darle una vuelta a la comprobación de saldo. Read uncomitted?
    public void onClickBtnTransferir(){
        if (campoPara == null){                   // Los fondos se retiran de la cuenta
            // Se comprueba que haya saldo suficiente
            if (Float.parseFloat(txtCantidad.getText()) > campoDe.getSaldo()){
                Main.mensaje("Saldo insuficiente", 5);
                return;
            }

            // Hay saldo suficiente. Se retiran los fondos.
            if (super.getDAO(UsuarioDAO.class).retirarSaldo(Float.parseFloat(txtCantidad.getText()), campoDe)){
                // Se actualiza la tabla.
                campoDe.setSaldo(campoDe.getSaldo() - Float.parseFloat(txtCantidad.getText()));
                tablaUsuarios.refresh();
            }
        } else if (campoDe == null){          // Se depositan fondos
            super.getDAO(UsuarioDAO.class).depositarSaldo(Integer.parseInt(txtCantidad.getText()), campoPara);
        } else {                                // Transferencia de una cuenta a otra
            // Se comprueba que haya saldo suficiente
            if ((Float.parseFloat(txtCantidad.getText()) > campoDe.getSaldo())){
                Main.mensaje("Saldo insuficiente", 5);
                return;
            }

            super.iniciarTransaccion();
            super.getDAO(UsuarioDAO.class).retirarSaldo(Float.parseFloat(txtCantidad.getText()), campoDe);
            super.getDAO(UsuarioDAO.class).depositarSaldo(Float.parseFloat(txtCantidad.getText()), campoPara);
            super.ejecutarTransaccion();
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
     * Elimina la opción de escoger un dato de la tabla.
     */
    public void onMouseReleasedTabla(){
        btnDeTabla.setVisible(false);
        btnParaTabla.setVisible(false);
    }

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
            stage.setOnCloseRequest(event -> actualizarRegistrosPendientes());
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
            stage.setOnCloseRequest(event -> actualizarBajasPendientes());
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
            stage.setOnCloseRequest(event -> actualizarOfertasPendientes());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
