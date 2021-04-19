package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.ErrorValidator;
import gal.sdc.usc.wallstreet.util.Validadores;
import gal.sdc.usc.wallstreet.util.auth.PasswordStorage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class OtpController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "otp";
    public static final Integer HEIGHT = 150;
    public static final Integer WIDTH = 425;

    @FXML
    public AnchorPane anchor;

    @FXML
    public VBox parent;

    @FXML
    private JFXTextField txtUsuario;

    @FXML
    private JFXPasswordField txtClave;

    @FXML
    private JFXButton btnRegistro;

    @FXML
    private JFXButton btnAcceso;

    public OtpController() {
    }

    private void acceder() {
        if (!txtUsuario.validate() || !txtClave.validate()) return;

        // Crear los validadores personalizado
        ErrorValidator usuarioNoExiste = Validadores.personalizado("El usuario no existe");
        ErrorValidator usuarioNoActivo = Validadores.personalizado("Este usuario no está activo");
        ErrorValidator claveIncorrecta = Validadores.personalizado("La clave no es correcta");

        Usuario usuario = super.getDAO(UsuarioDAO.class).seleccionar(txtUsuario.getText().toLowerCase());
        if (usuario == null) {
            if (txtUsuario.getValidators().size() == 1) txtUsuario.getValidators().add(usuarioNoExiste);
            txtUsuario.validate();
            return;
        }

        if (!usuario.getActivo()) {
            if (txtUsuario.getValidators().size() == 1) txtUsuario.getValidators().add(usuarioNoActivo);
            txtUsuario.validate();
            return;
        }

        try {
            if (!PasswordStorage.validarClave(txtClave.getText(), usuario.getClave())) {
                if (txtClave.getValidators().size() == 1) txtClave.getValidators().add(claveIncorrecta);
                txtClave.validate();
                return;
            }
        } catch (PasswordStorage.CannotPerformOperationException | PasswordStorage.InvalidHashException ex) {
            System.err.println(ex.getMessage());
        }

        boolean ok = false;
        if (usuario.getOtp() != null) {

        }

        // TODO: Usuario correcto, abrir ventana principal
        Inversor inversor = super.getDAO(InversorDAO.class).seleccionar(usuario);

        if (inversor != null) {
            super.setInversor(inversor);
        } else {
            Empresa empresa = super.getDAO(EmpresaDAO.class).seleccionar(usuario);
            super.setEmpresa(empresa);
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Añadir los validadores de requerido
        RequiredFieldValidator rfv = Validadores.requerido();
        txtUsuario.getValidators().add(rfv);
        txtClave.getValidators().add(rfv);

        txtUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario no existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtUsuario.getValidators().size() > 1) {
                txtUsuario.getValidators().remove(1);
                txtUsuario.validate();
            }
        });

        txtClave.textProperty().addListener((observable, oldValue, newValue) -> {
            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // contraseña incorrecta, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtClave.getValidators().size() > 1) {
                txtClave.getValidators().remove(1);
                txtClave.validate();
            }
        });

        btnRegistro.setOnAction(e -> Main.setScene(RegistroController.VIEW, RegistroController.WIDTH, RegistroController.HEIGHT));

        btnAcceso.setOnAction(e -> this.acceder());
    }
}
