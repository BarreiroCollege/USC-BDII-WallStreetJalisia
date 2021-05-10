package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.repository.SociedadDAO;
import gal.sdc.usc.wallstreet.repository.SuperUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import gal.sdc.usc.wallstreet.util.ErrorValidator;
import gal.sdc.usc.wallstreet.util.Validadores;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SociedadNuevaController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "sociedadnueva";
    public static final Integer HEIGHT = 200;
    public static final Integer WIDTH = 500;
    public static final String TITULO = "Crear Sociedad";
    private static Comunicador comunicador;
    @FXML
    public JFXButton btnCancelar;
    @FXML
    public JFXButton btnCrear;
    @FXML
    private AnchorPane anchor;
    @FXML
    private JFXTextField txtIdentificador;

    public static void setComunicador(Comunicador comunicador) {
        SociedadNuevaController.comunicador = comunicador;
    }

    private void crear(ActionEvent e) {
        if (!txtIdentificador.validate()) return;

        ErrorValidator usuarioYaExiste = Validadores.personalizado("Este nombre ya está en uso");

        if (super.getDAO(SuperUsuarioDAO.class).seleccionar(txtIdentificador.getText().toLowerCase()) != null) {
            if (txtIdentificador.getValidators().size() == 1) txtIdentificador.getValidators().add(usuarioYaExiste);
            txtIdentificador.validate();
            return;
        }

        super.iniciarTransaccion();

        Sociedad s = new Sociedad.Builder()
                .withSuperUsuario(new SuperUsuario.Builder(txtIdentificador.getText()).build())
                .build();

        super.getDAO(SociedadDAO.class).insertar(s);

        super.getUsuarioSesion().getUsuario().setLider(true);
        super.getUsuarioSesion().getUsuario().setSociedad(s);
        super.getDAO(UsuarioDAO.class).actualizar(super.getUsuarioSesion().getUsuario());

        if (super.ejecutarTransaccion()) {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
            Main.mensaje("Se ha creado la sociedad");
        } else {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onFailure();
            comunicador = null;
            Main.mensaje("Hubo un error creando la sociedad");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RequiredFieldValidator rfv = Validadores.requerido();
        txtIdentificador.getValidators().add(rfv);

        txtIdentificador.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limitar a 16 caracteres
            if (!newValue.matches("[a-zA-Z0-9_]{0,16}")) {
                txtIdentificador.setText(oldValue);
            }

            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario ya existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtIdentificador.getValidators().size() > 1) {
                txtIdentificador.getValidators().remove(1);
                txtIdentificador.validate();
            }
        });

        btnCancelar.setOnAction(e -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onFailure();
            comunicador = null;
        });


        txtIdentificador.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) this.crear(null);
        });

        btnCrear.setOnAction(this::crear);
    }
}
