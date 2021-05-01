package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.PropuestaCompra;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.PropuestaCompraDAO;
import gal.sdc.usc.wallstreet.repository.SuperUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import gal.sdc.usc.wallstreet.util.ErrorValidator;
import gal.sdc.usc.wallstreet.util.Validadores;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class SociedadPropuestaController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "sociedadpropuesta";
    public static final Integer HEIGHT = 200;
    public static final Integer WIDTH = 500;
    public static final String TITULO = "Nueva Propuesta";

    @FXML
    private AnchorPane anchor;

    // @FXML
    // private JFXTextField txtEmpresa;
    @FXML
    private JFXComboBox<Empresa> cmbEmpresa;
    @FXML
    private JFXTextField txtCantidad;
    @FXML
    private JFXTextField txtPrecioMax;

    @FXML
    public JFXButton btnCancelar;
    @FXML
    public JFXButton btnCrear;

    private static Comunicador comunicador;

    public static void setComunicador(Comunicador comunicador) {
        SociedadPropuestaController.comunicador = comunicador;
    }

    private void onBtnCrear(ActionEvent ae) {
        if (/* !txtEmpresa.validate() || */!txtCantidad.validate() || !txtPrecioMax.validate()) return;

        /* ErrorValidator usuarioYaExiste = Validadores.personalizado("Este identificador de empresa no existe");
        if (super.getDAO(EmpresaDAO.class).seleccionar(
                new Usuario.Builder(new SuperUsuario.Builder(txtEmpresa.getText().toLowerCase()).build()).build()
        ) == null) {
            if (txtEmpresa.getValidators().size() == 1) txtEmpresa.getValidators().add(usuarioYaExiste);
            txtEmpresa.validate();
            return;
        } */

        ErrorValidator empresaNoHay = Validadores.personalizado("Selecciona una empresa");
        ErrorValidator numeroNoValido = Validadores.personalizado("Introduce un precio válido");
        Empresa e = cmbEmpresa.getValue();

        float precioMax;
        try {
            precioMax = Float.parseFloat(txtPrecioMax.getText());
        } catch (NumberFormatException ex) {
            if (txtPrecioMax.getValidators().size() == 1) txtPrecioMax.getValidators().add(numeroNoValido);
            txtPrecioMax.validate();
            return;
        }

        int cantidad;
        try {
            cantidad = txtCantidad.getText().length() == 0 ? 0 : Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException ex) {
            if (txtCantidad.getValidators().size() == 1) txtCantidad.getValidators().add(numeroNoValido);
            txtCantidad.validate();
            return;
        }

        PropuestaCompra pc = new PropuestaCompra.Builder()
                .withSociedad((Sociedad) comunicador.getData()[0])
                .withEmpresa(e)
                .withFechaInicio(new Date())
                .withCantidad(cantidad == 0 ? null : cantidad)
                .withPrecioMax(precioMax)
                .build();

        if (super.getDAO(PropuestaCompraDAO.class).insertar(pc)) {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
            Main.mensaje("Se ha creado la propuesta de venta");
        } else {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onFailure();
            comunicador = null;
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Empresa> empresas = super.getDAO(EmpresaDAO.class).getEmpresas();

        Callback<ListView<Empresa>, ListCell<Empresa>> cellFactory = new Callback<ListView<Empresa>, ListCell<Empresa>>() {
            @Override
            public ListCell<Empresa> call(ListView<Empresa> l) {
                return new ListCell<Empresa>() {
                    @Override
                    protected void updateItem(Empresa item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("");
                        } else {
                            setText(item.getNombre() + " | " + item.getCif());
                        }
                    }
                } ;
            }
        };
        cmbEmpresa.setButtonCell(cellFactory.call(null));
        cmbEmpresa.setCellFactory(cellFactory);
        cmbEmpresa.getItems().addAll(empresas);
        cmbEmpresa.getSelectionModel().selectFirst();

        RequiredFieldValidator rfv = Validadores.requerido();
        // txtEmpresa.getValidators().add(rfv);
        txtCantidad.getValidators().add(rfv);
        // txtPrecioMax.getValidators().add(rfv);

        /* txtEmpresa.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limitar a 16 caracteres
            if (!newValue.matches("[a-zA-Z0-9_]{0,16}")) {
                txtEmpresa.setText(oldValue);
            }

            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario ya existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtEmpresa.getValidators().size() > 1) {
                txtEmpresa.getValidators().remove(1);
                txtEmpresa.validate();
            }
        }); */

        txtCantidad.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limitar a 16 caracteres
            if (!newValue.matches("[0-9]*")) {
                txtCantidad.setText(oldValue);
            }

            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario ya existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtCantidad.getValidators().size() > 1) {
                txtCantidad.getValidators().remove(1);
                txtCantidad.validate();
            }
        });

        txtPrecioMax.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limitar a 16 caracteres
            if (!newValue.matches("[0-9.,]*")) {
                txtPrecioMax.setText(oldValue);
            }

            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario ya existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtPrecioMax.getValidators().size() > 0) {
                txtPrecioMax.getValidators().remove(0);
                txtPrecioMax.validate();
            }
        });

        btnCancelar.setOnAction(e -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
        });

        btnCrear.setOnAction(this::onBtnCrear);
    }
}
