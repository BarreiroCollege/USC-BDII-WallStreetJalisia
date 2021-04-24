package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.repository.*;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ReguladorController extends DatabaseLinker {
    @FXML
    public Label txt_solicitudes_registro;
    @FXML
    public Label txt_solicitudes_baja;
    @FXML
    public Label txt_solicitudes_oferta;
    @FXML
    public Label txt_saldo;
    @FXML
    public JFXButton btn_ver_registros;
    @FXML
    public JFXButton btn_ver_bajas;
    @FXML
    public JFXButton btn_ver_ofertas;
    @FXML
    public JFXButton btn_actualizar_datos;

    private final String error = "error";

    @FXML
    public void initialize() {
        actualizarDatos();
        //actualizarSaldo();
    }

    public void actualizarDatos() {
        // Consultas en la base de datos
        // Número de usuarios que han solicitado registrarse y están pendientes de ser revisados
        Integer registrosPendientes = super.getDAO(UsuarioDAO.class).getNumInactivos();
        // Número de usuarios que han solicitado darse de baja y están pendientes de ser revisados
        Integer bajasPendientes = super.getDAO(UsuarioDAO.class).getNumSolicitudesBaja();
        // Número de ofertas de venta que no han sido aprobadas
        Integer ofertasPendientes = super.getDAO(OfertaVentaDAO.class).getNumOfertasPendientes();

        // Se muestra dicha información si no ha habido un error
        txt_solicitudes_registro.setText(registrosPendientes == null ? error : registrosPendientes.toString());
        txt_solicitudes_baja.setText(bajasPendientes == null ? error : bajasPendientes.toString());
        txt_solicitudes_oferta.setText(ofertasPendientes == null ? error : ofertasPendientes.toString());

        btn_ver_registros.setVisible(registrosPendientes != null && !registrosPendientes.equals(10));
        btn_ver_bajas.setVisible(bajasPendientes != null && !bajasPendientes.equals(0));
        btn_ver_ofertas.setVisible(ofertasPendientes != null && !ofertasPendientes.equals(0));

        actualizarSaldo();
    }

    public void actualizarSaldo() {

    }

    public void mostrarRevisarRegistros() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../view/revisarRegistros.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Peticiones de registro");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarRevisarBajas(){}

    public void mostrarRevisarOfertasVenta(){}
}
