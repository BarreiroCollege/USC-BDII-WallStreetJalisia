package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class RevisarRegistros extends DatabaseLinker {

    @FXML
    public Label txt_id;
    @FXML
    public Button btn_aceptar;
    @FXML
    public Button btn_rechazar;
    @FXML
    public Label txt_direccion;
    @FXML
    public Label txt_cp;
    @FXML
    public Label txt_localidad;
    @FXML
    public Label txt_tlf;
    @FXML
    public Label txt_subusuario;
    @FXML
    public Label txt_nombre;
    @FXML
    public Label txt_apellidos;
    @FXML
    public Label txt_titulo_id_subclase;
    @FXML
    public Label txt_id_subclase;
    @FXML
    public Button btn_siguiente;
    @FXML
    public Button btn_anterior;

    public List<Usuario> usuariosPendientes;
    public Usuario usuarioActual;


    public void initialize() {
        obtenerDatos();
        usuarioActual = usuariosPendientes.get(0);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    private void obtenerDatos() {
        usuariosPendientes = super.getDAO(InversorDAO.class).getInversoresPendientes();
        usuariosPendientes.addAll(super.getDAO(EmpresaDAO.class).getEmpresasPendientes());
        // TODO: ordenar
    }

    private void mostrarDatos() {
        txt_id.setText(usuarioActual.getSuperUsuario().getIdentificador());
        txt_direccion.setText(usuarioActual.getDireccion() == null ? "N/A" : usuarioActual.getDireccion());
        txt_cp.setText(usuarioActual.getCp() == null ? "N/A" : usuarioActual.getCp());
        txt_localidad.setText(usuarioActual.getLocalidad() == null ? "N/A" : usuarioActual.getLocalidad());
        txt_tlf.setText(usuarioActual.getTelefono() == null ? "N/A" : usuarioActual.getTelefono().toString());

        if (usuarioActual instanceof Inversor) {
            txt_subusuario.setText("Inversor");
            txt_titulo_id_subclase.setText("DNI:");
            txt_id_subclase.setText(((Inversor) usuarioActual).getDni());
            txt_nombre.setText(((Inversor) usuarioActual).getNombre());
            txt_apellidos.setText(((Inversor) usuarioActual).getApellidos());
            txt_apellidos.setVisible(true);
        } else {
            txt_subusuario.setText("Empresa");
            txt_titulo_id_subclase.setText("CIF:");
            txt_id_subclase.setText(((Empresa) usuarioActual).getCif());
            txt_nombre.setText(((Empresa) usuarioActual).getNombre());
            txt_apellidos.setVisible(false);
        }
    }

    public void controlarVisibilidadesAnteriorPosterior() {
        btn_siguiente.setVisible(usuariosPendientes.indexOf(usuarioActual) != usuariosPendientes.size() - 1);
        btn_anterior.setVisible(usuariosPendientes.indexOf(usuarioActual) != 0);
    }

    public void anterior() {
        usuarioActual = usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) - 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void siguiente() {
        usuarioActual = usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) + 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void aceptar() {
        super.getDAO(UsuarioDAO.class).aceptarUsuario(usuarioActual.getSuperUsuario().getIdentificador());
        if (usuariosPendientes.indexOf(usuarioActual) != usuariosPendientes.size() - 1){
            siguiente();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) - 1));
        } else if (usuariosPendientes.indexOf(usuarioActual) != 0){
            anterior();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) + 1));
        } else {
            cerrarVentana();
        }
    }

    public void cerrarVentana(){
        Stage stage = (Stage) btn_siguiente.getScene().getWindow();
        stage.close();
    }

    public void rechazar() {
        // TODO: mis 2 neuronas no encuentran los métodos por defecto esos
        //super.getDAO(UsuarioDAO.class).borrarUsuario(usuarioActual);
    }
}
