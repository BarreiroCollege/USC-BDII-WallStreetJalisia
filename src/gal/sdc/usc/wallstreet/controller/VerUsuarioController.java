package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.util.Comunicador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class VerUsuarioController {

    private static Comunicador comunicador;         // A través de él se recibe la información del usuario a mostrar
    @FXML
    private Label txtPrincipalNombre;
    @FXML
    private Label txtId;
    @FXML
    private Label txtDireccion;
    @FXML
    private Label txtCp;
    @FXML
    private Label txtLocalidad;
    @FXML
    private Label txtTlf;
    @FXML
    private Label txtSaldo;
    @FXML
    private Label txtSaldoBloqueado;
    @FXML
    private Label txtTipo;
    @FXML
    private Label txtTituloDniCif;           // Indicación de si se muestra un DNI o un CIF
    @FXML
    private Label txtDniCif;                  // DNI o CIF del usuario
    @FXML
    private Label txtNombre;
    @FXML
    private Label txtTituloApellidos;         // Indicación de apellidos (no aparece para la empresa)
    @FXML
    private Label txtApellidos;                // Apellidos del inversor

    public static void setComunicador(Comunicador comunicador) {
        VerUsuarioController.comunicador = comunicador;
    }

    public void initialize() {
        // Si no se ha recibido información, no se puede mostrar nada
        if (comunicador == null) ((Stage) txtApellidos.getScene().getWindow()).close();

        Object[] objeto = comunicador.getData();

        // El layout varía en función de si el usuario es un inversor o una empresa
        if (objeto[0] instanceof Empresa) {
            mostrarDatosUsuario((Empresa) objeto[0]);
            mostrarDatosEmpresa((Empresa) objeto[0]);
        } else {
            mostrarDatosUsuario((Inversor) objeto[0]);
            mostrarDatosInversor((Inversor) objeto[0]);
        }
    }

    // Datos comunes a todos los usuarios
    private void mostrarDatosUsuario(Inversor inversor) {
        txtId.setText(inversor.getUsuario().getSuperUsuario().getIdentificador());
        txtDireccion.setText(inversor.getUsuario().getDireccion());
        txtCp.setText(inversor.getUsuario().getCp());
        txtLocalidad.setText(inversor.getUsuario().getLocalidad());
        txtTlf.setText(inversor.getUsuario().getTelefono().toString());
        txtSaldo.setText(inversor.getUsuario().getSaldo().toString());
        txtSaldoBloqueado.setText(inversor.getUsuario().getSaldoBloqueado().toString());
    }

    // Datos comunes a todos los usuarios
    private void mostrarDatosUsuario(Empresa empresa) {
        txtId.setText(empresa.getUsuario().getSuperUsuario().getIdentificador());
        txtDireccion.setText(empresa.getUsuario().getDireccion());
        txtCp.setText(empresa.getUsuario().getCp());
        txtLocalidad.setText(empresa.getUsuario().getLocalidad());
        txtTlf.setText(empresa.getUsuario().getTelefono().toString());
        txtSaldo.setText(empresa.getUsuario().getSaldo().toString());
        txtSaldoBloqueado.setText(empresa.getUsuario().getSaldoBloqueado().toString());
    }

    // Datos propios de las empresas
    private void mostrarDatosEmpresa(Empresa empresa) {
        txtPrincipalNombre.setText(empresa.getNombre());
        txtTipo.setText("Empresa");
        txtTituloDniCif.setText("CIF:");
        txtDniCif.setText(empresa.getCif());
        txtNombre.setText(empresa.getNombre());
        txtTituloApellidos.setVisible(false);
        txtApellidos.setVisible(false);
    }

    // Datos propios del inversor
    private void mostrarDatosInversor(Inversor inversor) {
        txtPrincipalNombre.setText(inversor.getNombre() + " " + inversor.getApellidos());
        txtTipo.setText("Inversor");
        txtTituloDniCif.setText("DNI:");
        txtDniCif.setText(inversor.getDni());
        txtNombre.setText(inversor.getNombre());
        txtTituloApellidos.setVisible(true);
        txtApellidos.setVisible(true);
        txtApellidos.setText(inversor.getApellidos());
    }
}
