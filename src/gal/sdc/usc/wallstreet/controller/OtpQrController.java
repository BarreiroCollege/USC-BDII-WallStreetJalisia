package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import gal.sdc.usc.wallstreet.util.auth.GoogleAuth;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class OtpQrController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "otpqr";
    public static final Integer HEIGHT = 650;
    public static final Integer WIDTH = 350;
    public static final String TITULO = "Código QR";

    private static Comunicador comunicador;
    @FXML
    public AnchorPane anchor;
    @FXML
    public JFXTextField txtClavePrivada;
    @FXML
    private ImageView imgQr;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXButton btnSiguiente;

    public OtpQrController() {
    }

    public static void setComunicador(Comunicador comunicador) {
        OtpQrController.comunicador = comunicador;
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        String usuario = (String) comunicador.getData()[0];
        String clave = (String) comunicador.getData()[1];

        imgQr.setImage(new Image(Objects.requireNonNull(GoogleAuth.obtenerCodigoQR(usuario, clave))));
        imgQr.setFitHeight(300);
        imgQr.setFitWidth(300);

        txtClavePrivada.setText(clave);
        txtClavePrivada.setDisable(true);

        btnCancelar.setOnAction(event -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onFailure();
            comunicador = null;
        });

        btnSiguiente.setOnAction(event -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
        });
    }
}
