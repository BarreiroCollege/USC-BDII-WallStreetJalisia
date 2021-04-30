package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.PropuestaCompra;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.PropuestaCompraDAO;
import gal.sdc.usc.wallstreet.repository.SociedadDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import gal.sdc.usc.wallstreet.util.ErrorValidator;
import gal.sdc.usc.wallstreet.util.Validadores;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class SociedadSaldoController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "sociedadsaldo";
    public static final Integer HEIGHT = 200;
    public static final Integer WIDTH = 500;
    public static final String TITULO = "Transferir Saldo";

    @FXML
    private AnchorPane anchor;

    @FXML
    private JFXTextField txtSaldoDisponible;
    @FXML
    private JFXTextField txtSaldo;

    @FXML
    public JFXButton btnCancelar;
    @FXML
    public JFXButton btnTransferir;

    private static Comunicador comunicador;

    public static void setComunicador(Comunicador comunicador) {
        SociedadSaldoController.comunicador = comunicador;
    }

    private void onBtnTransferir(ActionEvent ae) {
        if (!txtSaldo.validate()) return;

        ErrorValidator noHaySaldo = Validadores.personalizado("No hay tanto saldo disponible");
        ErrorValidator numeroNoValido = Validadores.personalizado("Introduce un precio válido");

        float saldo;
        try {
            saldo = Float.parseFloat(txtSaldo.getText());
        } catch (NumberFormatException ex) {
            if (txtSaldo.getValidators().size() == 1) txtSaldo.getValidators().add(numeroNoValido);
            txtSaldo.validate();
            return;
        }

        Usuario u = super.getUsuarioSesion().getUsuario();
        if (saldo > u.getSaldoDisponible()) {
            if (txtSaldo.getValidators().size() == 1) txtSaldo.getValidators().add(noHaySaldo);
            txtSaldo.validate();
            return;
        }

        Sociedad s = (Sociedad) comunicador.getData()[0];
        s.setSaldoComunal(s.getSaldoComunal() + saldo);
        u.setSaldo(u.getSaldo() - saldo);

        super.iniciarTransaccion();
        super.getDAO(UsuarioDAO.class).actualizar(u);
        super.getDAO(SociedadDAO.class).actualizar(s);
        if (super.ejecutarTransaccion()) {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
            Main.mensaje("El saldo ha sido transferido");
        } else {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onFailure();
            comunicador = null;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RequiredFieldValidator rfv = Validadores.requerido();
        txtSaldo.getValidators().add(rfv);

        System.out.println(super.getUsuarioSesion().getUsuario());
        txtSaldoDisponible.setText(super.getUsuarioSesion().getUsuario().getSaldoDisponible().toString());

        txtSaldo.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limitar a 16 caracteres
            if (!newValue.matches("[0-9.,]*")) {
                txtSaldo.setText(oldValue);
            }

            // Si hay más de un validador, es porque se ha insertado el "forzado" para mostrar error de
            // usuario ya existe, y por ello, se ha de eliminar cuando se actualice el campo
            if (txtSaldo.getValidators().size() > 1) {
                txtSaldo.getValidators().remove(1);
                txtSaldo.validate();
            }
        });

        btnCancelar.setOnAction(e -> {
            ((Stage) anchor.getScene().getWindow()).close();
            comunicador.onSuccess();
            comunicador = null;
        });

        btnTransferir.setOnAction(this::onBtnTransferir);
    }
}
