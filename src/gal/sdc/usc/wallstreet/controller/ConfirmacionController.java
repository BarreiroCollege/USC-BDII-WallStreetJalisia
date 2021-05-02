package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmacionController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "confirmacion";
    public static final Integer HEIGHT = 150;
    public static final Integer WIDTH = 500;
    public static final String TITULO = "Confirmar";

    private static Comunicador comunicador;
    @FXML
    public AnchorPane anchor;
    @FXML
    private Label lblTitulo;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXButton btnAceptar;

    public ConfirmacionController() {
    }

    public static void setComunicador(Comunicador comunicador) {
        ConfirmacionController.comunicador = comunicador;
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        lblTitulo.setText((String) comunicador.getData()[0]);

        btnCancelar.setOnAction(event -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onFailure();
            comunicador = null;
        });

        btnAceptar.setOnAction(event -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
        });
    }
}
