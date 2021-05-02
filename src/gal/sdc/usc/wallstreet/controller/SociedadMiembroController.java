package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
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

public class SociedadMiembroController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "sociedadmiembro";
    public static final Integer HEIGHT = 200;
    public static final Integer WIDTH = 500;
    public static final String TITULO = "Invitar Miembro";
    private static Comunicador comunicador;
    @FXML
    public JFXButton btnCancelar;
    @FXML
    public JFXButton btnInvitar;
    @FXML
    private AnchorPane anchor;
    @FXML
    private JFXTextField txtIdentificador;

    public static void setComunicador(Comunicador comunicador) {
        SociedadMiembroController.comunicador = comunicador;
    }

    private void invitar(ActionEvent e) {
        if (!txtIdentificador.validate()) return;

        ErrorValidator usuarioNoExiste = Validadores.personalizado("Este usuario no existe");
        ErrorValidator usuarioYaSociedad = Validadores.personalizado("Este usuario ya está en una sociedad");

        Usuario u = super.getDAO(UsuarioDAO.class).seleccionar(
                new SuperUsuario.Builder(txtIdentificador.getText().toLowerCase()).build()
        );

        if (u == null) {
            if (txtIdentificador.getValidators().size() == 1) txtIdentificador.getValidators().add(usuarioNoExiste);
            txtIdentificador.validate();
            return;
        }

        if (u.getSociedad() != null) {
            if (txtIdentificador.getValidators().size() == 1) txtIdentificador.getValidators().add(usuarioYaSociedad);
            txtIdentificador.validate();
            return;
        }

        u.setSociedad((Sociedad) comunicador.getData()[0]);

        if (super.getDAO(UsuarioDAO.class).actualizar(u)) {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
            Main.mensaje("El usuario ha sido invitado");
        } else {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onFailure();
            comunicador = null;
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

        txtIdentificador.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) this.invitar(null);
        });

        btnCancelar.setOnAction(e -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
        });

        btnInvitar.setOnAction(this::invitar);
    }
}
