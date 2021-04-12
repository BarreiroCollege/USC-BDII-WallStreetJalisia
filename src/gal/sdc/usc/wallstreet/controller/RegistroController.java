package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistroController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "registro";
    public static final Integer HEIGHT = 700;
    public static final Integer WIDTH = 600;

    @FXML
    private JFXTextField txtUsuario;
    @FXML
    private JFXPasswordField txtClave;
    @FXML
    private JFXTextArea txtDireccion;
    @FXML
    private JFXTextField txtLocalidad;
    @FXML
    private JFXTextField txtCp;
    @FXML
    private JFXTextField txtTelefono;

    @FXML
    private JFXTabPane tabTipo;

    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextField txtApellidos;
    @FXML
    private JFXTextField txtDni;

    @FXML
    private JFXTextField txtEmpresa;
    @FXML
    private JFXTextField txtCif;

    @FXML
    private JFXButton btnRegistro;
    @FXML
    private JFXButton btnAcceso;

    private ErrorValidator usuarioNoValido;
    private ErrorValidator usuarioYaExiste;
    private ErrorValidator claveDebil;

    public RegistroController() {
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Añadir los validadores de requerido
        RequiredFieldValidator rfv = Validadores.requerido();
        txtUsuario.getValidators().add(rfv);
        txtClave.getValidators().add(rfv);
        txtDireccion.getValidators().add(rfv);
        txtLocalidad.getValidators().add(rfv);
        txtCp.getValidators().add(rfv);
        txtTelefono.getValidators().add(rfv);

        txtNombre.getValidators().add(rfv);
        txtApellidos.getValidators().add(rfv);
        txtDni.getValidators().add(rfv);

        IntegerValidator iv = new IntegerValidator("Introduce un número de teléfono válido");
        txtTelefono.getValidators().add(iv);

        tabTipo.getSelectionModel().selectedItemProperty().addListener(listener -> {
            switch (tabTipo.getSelectionModel().getSelectedIndex()) {
                case 0:
                    txtNombre.getValidators().add(rfv);
                    txtApellidos.getValidators().add(rfv);
                    txtDni.getValidators().add(rfv);
                    txtEmpresa.getValidators().remove(0);
                    txtCif.getValidators().remove(0);
                    break;
                case 1:
                    txtNombre.getValidators().remove(0);
                    txtApellidos.getValidators().remove(0);
                    txtDni.getValidators().remove(0);
                    txtEmpresa.getValidators().add(rfv);
                    txtCif.getValidators().add(rfv);
                    break;
            }
        });

        txtUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario ya existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtUsuario.getValidators().size() > 1) {
                txtUsuario.getValidators().remove(1);
                txtUsuario.validate();
            }
        });

        txtClave.textProperty().addListener((observable, oldValue, newValue) -> {
            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // contraseña demasiado dábil, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtClave.getValidators().size() > 1) {
                txtClave.getValidators().remove(1);
                txtClave.validate();
            }
        });

        btnAcceso.setOnAction(e -> Main.setScene(AccesoController.VIEW, AccesoController.WIDTH, AccesoController.HEIGHT));
        btnRegistro.setOnAction(this::registrar);

        this.usuarioNoValido = Validadores.personalizado("Sólo puede tener numeros y letras");
        this.usuarioYaExiste = Validadores.personalizado("Este usuario ya existe");
        this.claveDebil = Validadores.personalizado("Esta clave es demasiado débil");
    }

    private void registrar(ActionEvent e) {
        if (!txtUsuario.validate() || !txtClave.validate() || !txtDireccion.validate() || !txtCp.validate()
                || !txtLocalidad.validate() || !txtTelefono.validate()) return;

        if (tabTipo.getSelectionModel().getSelectedIndex() == 0) {
            if (!txtNombre.validate() || !txtApellidos.validate() || !txtDni.validate()) return;
        } else {
            if (!txtEmpresa.validate() || !txtCif.validate()) return;
        }

        if (!txtUsuario.getText().matches("[A-Za-z0-9]+")) {
            if (txtUsuario.getValidators().size() == 1) txtUsuario.getValidators().add(usuarioNoValido);
            txtUsuario.validate();
            return;
        }

        if (super.getDAO(UsuarioDAO.class).seleccionar(txtUsuario.getText().toLowerCase()) != null) {
            if (txtUsuario.getValidators().size() == 1) txtUsuario.getValidators().add(usuarioYaExiste);
            txtUsuario.validate();
            return;
        }

        if (txtClave.getText().length() < 8) {
            if (txtClave.getValidators().size() == 1) txtClave.getValidators().add(claveDebil);
            txtClave.validate();
            return;
        }

        try {
            Usuario usuario = new Usuario.Builder(txtUsuario.getText())
                    .withClave(PasswordStorage.crearHash(txtClave.getText()))
                    .withDireccion(txtDireccion.getText())
                    .withCp(txtCp.getText())
                    .withLocalidad(txtLocalidad.getText())
                    .withTelefono(Integer.parseInt(txtTelefono.getText()))
                    .build();

            boolean ok;
            if (tabTipo.getSelectionModel().getSelectedIndex() == 0) {
                Inversor inversor = new Inversor.Builder(usuario)
                        .withNombre(txtNombre.getText())
                        .withApellidos(txtApellidos.getText())
                        .withDni(txtDni.getText())
                        .build();
                ok = super.getDAO(InversorDAO.class).insertar(inversor);
            } else {
                Empresa empresa = new Empresa.Builder(usuario)
                        .withNombre(txtEmpresa.getText())
                        .withCif(txtCif.getText())
                        .build();
                ok = super.getDAO(EmpresaDAO.class).insertar(empresa);
            }

            if (ok) {
                Main.setScene(AccesoController.VIEW, AccesoController.WIDTH, AccesoController.HEIGHT);
                Main.mensaje("Se ha creado la cuenta con éxito", 5);
            } else {
                Main.mensaje("Error creando la cuenta");
            }
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
