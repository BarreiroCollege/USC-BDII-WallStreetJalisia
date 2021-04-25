package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.util.Comunicador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class VerUsuarioController {
    @FXML
    private Label txt_titulo_apellidos;
    @FXML
    private Label txt_dni_cif;
    @FXML
    private Label txt_nombre;
    @FXML
    private Label txt_apellidos;
    @FXML
    private Label txt_titulo_dni_cif;
    @FXML
    private Label txt_tipo;
    @FXML
    private Label txt_principal_nombre;
    @FXML
    private Label txt_id;
    @FXML
    private Label txt_direccion;
    @FXML
    private Label txt_cp;
    @FXML
    private Label txt_localidad;
    @FXML
    private Label txt_tlf;

    private static Comunicador comunicador;

    public static void setComunicador(Comunicador comunicador){
        VerUsuarioController.comunicador = comunicador;
    }

    public void initialize(){
        if (comunicador == null) ((Stage) txt_apellidos.getScene().getWindow()).close();

        Object[] objeto = comunicador.getData();

        mostrarDatosUsuario((Usuario) objeto[0]);

        if (objeto[0] instanceof Empresa) mostrarDatosEmpresa((Empresa) objeto[0]);
        else mostrarDatosInversor((Inversor) objeto[0]);
    }

    private void mostrarDatosUsuario(Usuario usuario){
        txt_id.setText(usuario.getIdentificador());
        txt_direccion.setText(usuario.getDireccion());
        txt_cp.setText(usuario.getCp());
        txt_localidad.setText(usuario.getLocalidad());
        txt_tlf.setText(usuario.getTelefono().toString());
    }

    private void mostrarDatosEmpresa(Empresa empresa){
        txt_principal_nombre.setText(empresa.getNombre());
        txt_tipo.setText("Empresa");
        txt_titulo_dni_cif.setText("CIF:");
        txt_dni_cif.setText(empresa.getCif());
        txt_nombre.setText(empresa.getNombre());
        txt_titulo_apellidos.setVisible(false);
        txt_apellidos.setVisible(false);
    }

    private void mostrarDatosInversor(Inversor inversor){
        txt_principal_nombre.setText(inversor.getNombre() + " " + inversor.getApellidos());
        txt_tipo.setText("Inversor");
        txt_titulo_dni_cif.setText("DNI:");
        txt_dni_cif.setText(inversor.getDni());
        txt_nombre.setText(inversor.getNombre());
        txt_titulo_apellidos.setVisible(true);
        txt_apellidos.setVisible(true);
        txt_apellidos.setText(inversor.getApellidos());
    }
}
