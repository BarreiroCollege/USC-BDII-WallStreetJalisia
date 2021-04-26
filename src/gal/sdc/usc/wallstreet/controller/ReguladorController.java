package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ReguladorController extends DatabaseLinker {
    @FXML
    private TableColumn<Usuario, String> columnaId;
    @FXML
    private TableColumn<Usuario, String> columnaSaldo;
    @FXML
    private ChoiceBox cbTipo;
    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField DniCif;
    @FXML
    private JFXTextField Nombre;
    @FXML
    private JFXTextField Apellidos;
    @FXML
    private Label txt_solicitudes_registro;
    @FXML
    private Label txt_solicitudes_baja;
    @FXML
    private Label txt_solicitudes_oferta;
    @FXML
    private Label txt_saldo;
    @FXML
    private JFXButton btn_ver_registros;
    @FXML
    private JFXButton btn_ver_bajas;
    @FXML
    private JFXButton btn_ver_ofertas;
    @FXML
    private JFXButton btn_actualizar_datos;
    @FXML
    private JFXButton btn_aceptar_todo_registros;
    @FXML
    private JFXButton btn_aceptar_todo_bajas;
    @FXML
    private JFXButton btn_aceptar_todo_ofertas;
    @FXML
    private TableView<Usuario> tablaUsuarios;

    private final String error = "error";
    private static JFXSnackbar snackbar;
    private final ObservableList<Usuario> datosTabla = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        actualizarDatos();
        //actualizarSaldo();
        registrarDatos();
        establecerColumnasTabla();
    }

    public void actualizarDatos() {
        actualizarRegistrosPendientes();
        actualizarBajasPendientes();
        actualizarOfertasPendientes();
        actualizarSaldo();
    }

    public void registrarDatos(){
        List<Usuario> usuarios = super.getDAO(UsuarioDAO.class).getUsuariosActivos();
        datosTabla.setAll(usuarios);
        tablaUsuarios.setItems(datosTabla);
    }

    public void establecerColumnasTabla() {
        // Establecemos los valores que contendrá cada columna de la tabla de participaciones
        columnaId.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getIdentificador()));
        columnaSaldo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getSaldo().toString()));
    }

    public void actualizarRegistrosPendientes() {
        // Número de usuarios que han solicitado registrarse y están pendientes de ser revisados
        Integer registrosPendientes = super.getDAO(UsuarioDAO.class).getNumInactivos();
        // Se muestra dicha información si no ha habido un error
        txt_solicitudes_registro.setText(registrosPendientes == null ? error : registrosPendientes.toString());
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay registros pendientes
        btn_ver_registros.setVisible(registrosPendientes != null && !registrosPendientes.equals(0));
        btn_aceptar_todo_registros.setVisible(registrosPendientes != null && !registrosPendientes.equals(0));
    }

    public void actualizarBajasPendientes() {
        // Número de usuarios que han solicitado darse de baja y están pendientes de ser revisados
        Integer bajasPendientes = super.getDAO(UsuarioDAO.class).getNumSolicitudesBaja();
        // Se muestra dicha información si no ha habido un error
        txt_solicitudes_baja.setText(bajasPendientes == null ? error : bajasPendientes.toString());
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay bajas pendientes
        btn_ver_bajas.setVisible(bajasPendientes != null && !bajasPendientes.equals(0));
        btn_aceptar_todo_bajas.setVisible(bajasPendientes != null && !bajasPendientes.equals(0));
    }

    public void actualizarOfertasPendientes() {
        // Número de ofertas de venta que no han sido aprobadas
        Integer ofertasPendientes = super.getDAO(OfertaVentaDAO.class).getNumOfertasPendientes();
        // Se muestra dicha información si no ha habido un error
        txt_solicitudes_oferta.setText(ofertasPendientes == null ? error : ofertasPendientes.toString());
        // Se muestran los botones de revisión y aceptar si no ha habido un error y hay ofertas pendientes
        btn_ver_ofertas.setVisible(ofertasPendientes != null && !ofertasPendientes.equals(0));
        btn_aceptar_todo_ofertas.setVisible(ofertasPendientes != null && !ofertasPendientes.equals(0));
    }

    public void actualizarSaldo() {

    }

    public void aceptarTodoRegistros() {
        //TODO: transacción?
        //super.iniciarTransaccion();
        super.getDAO(UsuarioDAO.class).aceptarUsuariosTodos();
        //super.ejecutarTransaccion();
        actualizarRegistrosPendientes();
    }

    public void aceptarTodoBajas() {
        actualizarBajasPendientes();
    }

/*    public void aceptarTodoBajas(){
        super.iniciarTransaccion();
        Boolean sinRechazos = super.getDAO(UsuarioDAO.class).aceptarBajasTodas(
                super.getDAO(EmpresaDAO.class).getEmpresasBajasPendientes(),
                super.getDAO(InversorDAO.class).getInversoresBajasPendientes()
        );
        if (super.ejecutarTransaccion()){
            if (Boolean.FALSE.equals(sinRechazos)) {
                mensaje("Las bajas de cuentas con participaciones se han rechazado.", 5);
            }
            actualizarDatos();
        } else  {
            mensaje("Error en la gestión de bajas", 5);
        }
    }*/

    public void aceptarTodoOfertas() {
        actualizarOfertasPendientes();
    }

    public void mostrarRevisarRegistros() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/revisarRegistros.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Peticiones de registro");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 600, 400));
            // La ventana del regulador es la ventana padre. Queda visible, pero desactivada.
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btn_aceptar_todo_bajas.getScene().getWindow());
            stage.setOnCloseRequest(event -> actualizarRegistrosPendientes());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarRevisarBajas() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/revisarBajas.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Peticiones de baja");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 600, 400));
            // La ventana del regulador es la ventana padre. Queda visible, pero desactivada.
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btn_aceptar_todo_bajas.getScene().getWindow());
            stage.setOnCloseRequest(event -> actualizarBajasPendientes());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarRevisarOfertasVenta() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/revisarOfertasVenta.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ofertas de venta no confirmadas");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 550, 350));
            // La ventana del regulador es la ventana padre. Queda visible, pero desactivada.
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btn_aceptar_todo_bajas.getScene().getWindow());
            stage.setOnCloseRequest(event -> actualizarOfertasPendientes());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mensaje(String mensaje, Integer duracion) {
        mensaje(new JFXSnackbarLayout(mensaje), duracion);
    }

    private static void mensaje(JFXSnackbarLayout layout, Integer duracion) {
        JFXSnackbarLayout finalLayout = new JFXSnackbarLayout(layout.getToast(), "Cerrar", e -> snackbar.close());
        if (duracion != null) {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout, Duration.seconds(duracion)));
        } else {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout));
        }
    }
}
