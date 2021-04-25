package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.SuperUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import gal.sdc.usc.wallstreet.util.ErrorValidator;
import gal.sdc.usc.wallstreet.util.TipoUsuario;
import gal.sdc.usc.wallstreet.util.Validadores;
import gal.sdc.usc.wallstreet.util.auth.GoogleAuth;
import gal.sdc.usc.wallstreet.util.auth.PasswordStorage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PerfilController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "perfil";
    public static final Integer HEIGHT = 600;
    public static final Integer WIDTH = 800;
    public static final String TITULO = "Mi Perfil";

    private final BooleanProperty editando = new SimpleBooleanProperty(false);

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
    public VBox divInversor;
    @FXML
    public VBox divEmpresa;

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
    private JFXButton btnVolver;
    @FXML
    public JFXButton btnOtp;
    @FXML
    private JFXButton btnEditar;

    private String claveOtp;

    private ErrorValidator usuarioNoValido;
    private ErrorValidator usuarioYaExiste;
    private ErrorValidator claveDebil;

    public PerfilController() {
    }

    private void cambiarTextoBotonOtp() {
        if (super.getUsuarioSesion().getUsuario().getOtp() != null) {
            btnOtp.setText("Desactivar 2FA");
        } else {
            btnOtp.setText("Activar 2FA");
        }
    }

    private void onBtnEditar(ActionEvent e) {
        if (!editando.get()) {
            btnEditar.setText("Guardar");
            btnVolver.setText("Cancelar");
            editando.setValue(true);
        } else {
            this.guardar(e);
        }
    }

    private void confirmarOtp(boolean activar) {
        Usuario usuario = super.getUsuarioSesion().getUsuario();
        if (activar) usuario.setOtp(this.claveOtp);

        Comunicador comunicador = new Comunicador() {
            @Override
            public Object[] getData() {
                return new Object[] {usuario};
            }

            @Override
            public void onSuccess() {
                if (!activar) usuario.setOtp(null);
                PerfilController.super.getDAO(UsuarioDAO.class).actualizar(usuario);

                if (activar) {
                    Main.mensaje("Se ha activado la verificación en dos pasos", 5);
                } else {
                    Main.mensaje("Se ha desactivado la verificación en dos pasos", 5);
                }
                cambiarTextoBotonOtp();
            }

            @Override
            public void onFailure() {
                if (activar) {
                    claveOtp = null;
                    usuario.setOtp(null);
                }
            }
        };
        OtpController.setComunicador(comunicador);
        Main.dialogo(OtpController.VIEW, OtpController.WIDTH, OtpController.HEIGHT, OtpController.TITULO);
    }

    private void onBtnOtp(ActionEvent e) {
        if (super.getUsuarioSesion().getUsuario().getOtp() != null) {
            confirmarOtp(false);
        } else {
            this.claveOtp = GoogleAuth.generarClave();
            Comunicador comunicador = new Comunicador() {
                @Override
                public Object[] getData() {
                    return new Object[]{
                            PerfilController.super.getUsuarioSesion().getUsuario().getSuperUsuario().getIdentificador(),
                            claveOtp
                    };
                }

                @Override
                public void onSuccess() {
                    confirmarOtp(true);
                }

                @Override
                public void onFailure() {
                    claveOtp = null;
                }
            };
            OtpQrController.setComunicador(comunicador);
            Main.dialogo(OtpQrController.VIEW, OtpQrController.WIDTH, OtpQrController.HEIGHT, OtpQrController.TITULO);
        }
    }

    private void onBtnVolver(ActionEvent e) {
        if (!editando.get()) {
            // TODO: Mandar a Principal
        } else {
            this.asignarValores();
            editando.setValue(false);
            btnEditar.setText("Editar");
            btnVolver.setText("Volver");
        }
    }

    private void asignarValores() {
        UsuarioSesion us = super.getUsuarioSesion();
        Usuario u = us.getUsuario();

        txtUsuario.setText(u.getSuperUsuario().getIdentificador());
        txtClave.setText("");
        txtDireccion.setText(u.getDireccion());
        txtLocalidad.setText(u.getLocalidad());
        txtCp.setText(u.getCp());
        txtTelefono.setText(u.getTelefono().toString());

        if (super.getTipoUsuario().equals(TipoUsuario.INVERSOR)) {
            Inversor i = (Inversor) us;
            txtNombre.setText(i.getNombre());
            txtApellidos.setText(i.getApellidos());
            txtDni.setText(i.getDni());
        } else {
            Empresa e = (Empresa) us;
            txtEmpresa.setText(e.getNombre());
            txtCif.setText(e.getCif());
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Añadir los validadores de requerido
        RequiredFieldValidator rfv = Validadores.requerido();
        txtUsuario.getValidators().add(rfv);
        // txtClave.getValidators().add(rfv);
        txtDireccion.getValidators().add(rfv);
        txtLocalidad.getValidators().add(rfv);
        txtCp.getValidators().add(rfv);
        txtTelefono.getValidators().add(rfv);

        txtUsuario.disableProperty().bind(editando.not());
        txtClave.disableProperty().bind(editando.not());
        txtDireccion.disableProperty().bind(editando.not());
        txtLocalidad.disableProperty().bind(editando.not());
        txtCp.disableProperty().bind(editando.not());
        txtTelefono.disableProperty().bind(editando.not());

        btnOtp.visibleProperty().bind(editando.not());

        RegexValidator rgx = new RegexValidator("Introduce un número de teléfono válido");
        // rgx.setRegexPattern("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");
        rgx.setRegexPattern("^[-0-9]*$");
        txtTelefono.getValidators().add(rgx);

        switch (super.getTipoUsuario()) {
            case INVERSOR:
                divEmpresa.setVisible(false);

                txtNombre.getValidators().add(rfv);
                txtApellidos.getValidators().add(rfv);
                txtDni.getValidators().add(rfv);

                txtNombre.disableProperty().bind(editando.not());
                txtApellidos.disableProperty().bind(editando.not());
                break;
            case EMPRESA:
                divInversor.setVisible(false);
                txtEmpresa.getValidators().add(rfv);
                txtCif.getValidators().add(rfv);

                txtEmpresa.disableProperty().bind(editando.not());
                break;
        }

        cambiarTextoBotonOtp();

        txtUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limitar a 16 caracteres
            if (!newValue.matches("[a-zA-Z0-9_]{0,16}")) {
                txtUsuario.setText(oldValue);
            }

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
            if (txtClave.getValidators().size() > 0) {
                txtClave.getValidators().remove(0);
                txtClave.validate();
            }
        });

        btnEditar.setOnAction(this::onBtnEditar);
        btnVolver.setOnAction(this::onBtnVolver);
        btnOtp.setOnAction(this::onBtnOtp);

        this.usuarioNoValido = Validadores.personalizado("Sólo puede tener numeros y letras");
        this.usuarioYaExiste = Validadores.personalizado("Este usuario ya existe");
        this.claveDebil = Validadores.personalizado("Esta clave es demasiado débil");

        this.asignarValores();
    }

    private void guardar(ActionEvent e) {
        if (!txtUsuario.validate() || !txtClave.validate() || !txtDireccion.validate() || !txtCp.validate()
                || !txtLocalidad.validate() || !txtTelefono.validate()) return;

        if (super.getTipoUsuario().equals(TipoUsuario.INVERSOR)) {
            if (!txtNombre.validate() || !txtApellidos.validate() || !txtDni.validate()) return;
        } else {
            if (!txtEmpresa.validate() || !txtCif.validate()) return;
        }

        if (!txtUsuario.getText().matches("[A-Za-z0-9]+")) {
            if (txtUsuario.getValidators().size() == 1) txtUsuario.getValidators().add(usuarioNoValido);
            txtUsuario.validate();
            return;
        }

        Usuario u = super.getUsuarioSesion().getUsuario();
        if (!u.getSuperUsuario().getIdentificador().equals(txtUsuario.getText().toLowerCase())
                && super.getDAO(SuperUsuarioDAO.class).seleccionar(txtUsuario.getText().toLowerCase()) != null) {
            if (txtUsuario.getValidators().size() == 1) txtUsuario.getValidators().add(usuarioYaExiste);
            txtUsuario.validate();
            return;
        }

        if (txtClave.getText().length() > 0 && txtClave.getText().length() < 8) {
            if (txtClave.getValidators().size() == 1) txtClave.getValidators().add(claveDebil);
            txtClave.validate();
            return;
        }

        try {
            super.iniciarTransaccion();

            if (!txtUsuario.getText().toLowerCase().equals(u.getSuperUsuario().getIdentificador())) {
                super.getDAO(SuperUsuarioDAO.class).actualizarIdentificador(
                        u.getSuperUsuario().getIdentificador(),
                        txtUsuario.getText().toLowerCase()
                );
            }

            SuperUsuario superUsuario = new SuperUsuario.Builder()
                    .withIdentificador(txtUsuario.getText().toLowerCase())
                    .build();

            String clave = u.getClave();
            if (txtClave.getText().length() > 0) {
                clave = PasswordStorage.crearHash(txtClave.getText());
            }

            Usuario usuario = new Usuario.Builder(superUsuario)
                    .withClave(clave)
                    .withDireccion(txtDireccion.getText())
                    .withCp(txtCp.getText())
                    .withLocalidad(txtLocalidad.getText())
                    .withTelefono(Integer.parseInt(txtTelefono.getText()))
                    .withSaldo(u.getSaldo())
                    .withSaldoBloqueado(u.getSaldoBloqueado())
                    .withActivo(u.getActivo())
                    .withBaja(u.getBaja())
                    .withOtp(u.getOtp())
                    .withSociedad(u.getSociedad())
                    .withLider(u.getLider())
                    .build();

            if (super.getTipoUsuario().equals(TipoUsuario.INVERSOR)) {
                Inversor inversor = new Inversor.Builder(usuario)
                        .withNombre(txtNombre.getText())
                        .withApellidos(txtApellidos.getText())
                        .withDni(txtDni.getText())
                        .build();
                super.getDAO(InversorDAO.class).actualizar(inversor);
                super.setUsuarioSesion(inversor);
            } else {
                Empresa empresa = new Empresa.Builder(usuario)
                        .withNombre(txtEmpresa.getText())
                        .withCif(txtCif.getText())
                        .build();
                super.getDAO(EmpresaDAO.class).actualizar(empresa);
                super.setUsuarioSesion(empresa);
            }

            if (super.ejecutarTransaccion()) {
                editando.setValue(false);
                btnEditar.setText("Editar");
                btnVolver.setText("Volver");
                Main.mensaje("Se han actualizado los datos de la cuenta", 6);
            } else {
                Main.mensaje("Error actualizando los datos");
            }
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
