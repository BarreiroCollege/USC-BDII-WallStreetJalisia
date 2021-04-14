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
import gal.sdc.usc.wallstreet.util.PasswordStorage;
import gal.sdc.usc.wallstreet.util.Validadores;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AccesoController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "acceso";
    public static final Integer HEIGHT = 500;
    public static final Integer WIDTH = 400;

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

    public AccesoController() {
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Añadir los validadores de requerido
        RequiredFieldValidator rfv = Validadores.requerido();
        txtUsuario.getValidators().add(rfv);
        txtClave.getValidators().add(rfv);

        // Crear los validadores personalizado
        ErrorValidator usuarioNoExiste = Validadores.personalizado("El usuario no existe");
        ErrorValidator claveIncorrecta = Validadores.personalizado("La clave no es correcta");

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

        btnAcceso.setOnAction(e -> {
            if (!txtUsuario.validate() || !txtClave.validate()) return;

            Usuario usuario = super.getDAO(UsuarioDAO.class).seleccionar(txtUsuario.getText().toLowerCase());
            if (usuario == null) {
                if (txtUsuario.getValidators().size() == 1) txtUsuario.getValidators().add(usuarioNoExiste);
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

            // TODO: Usuario correcto, abrir ventana principal
            Inversor inversor = super.getDAO(InversorDAO.class).seleccionar(usuario);

            if (inversor != null) {
                super.setInversor(inversor);
            } else {
                Empresa empresa = super.getDAO(EmpresaDAO.class).seleccionar(usuario);
                super.setEmpresa(empresa);
            }

            Main.setScene(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT);
        });
    }
}
