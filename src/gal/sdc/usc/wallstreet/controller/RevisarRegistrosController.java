package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RevisarRegistrosController extends DatabaseLinker {

    @FXML
    private Label txt_id;
    @FXML
    private Button btn_aceptar;
    @FXML
    private Button btn_rechazar;
    @FXML
    private Label txt_direccion;
    @FXML
    private Label txt_cp;
    @FXML
    private Label txt_localidad;
    @FXML
    private Label txt_tlf;
    @FXML
    private Label txt_subusuario;
    @FXML
    private Label txt_nombre;
    @FXML
    private Label txt_apellidos;
    @FXML
    private Label txt_titulo_id_subclase;
    @FXML
    private Label txt_id_subclase;
    @FXML
    private Button btn_siguiente;
    @FXML
    private Button btn_anterior;

    public List<Usuario> usuariosPendientes;
    public Usuario usuarioActual;
    public String ordenElegido;

    public void initialize() {
        obtenerDatos();

        if (usuariosPendientes.size() > 1) abrirOrdenar();

        reordenar();
        usuarioActual = usuariosPendientes.get(0);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    private void obtenerDatos() {
        usuariosPendientes = super.getDAO(InversorDAO.class).getInversoresRegistrosPendientes();
        usuariosPendientes.addAll(super.getDAO(EmpresaDAO.class).getEmpresasRegistrosPendientes());
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
        if (usuariosPendientes.indexOf(usuarioActual) != usuariosPendientes.size() - 1) {
            siguiente();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) - 1));
            controlarVisibilidadesAnteriorPosterior();
        } else if (usuariosPendientes.indexOf(usuarioActual) != 0) {
            anterior();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) + 1));
            controlarVisibilidadesAnteriorPosterior();
        } else {
            cerrarVentana();
        }
    }

    public void cerrarVentana() {
        Stage stage = (Stage) btn_siguiente.getScene().getWindow();
        stage.close();
    }

    public void rechazar() {
        // TODO: mis 2 neuronas no encuentran los métodos por defecto esos
        //super.getDAO(UsuarioDAO.class).borrarUsuario(usuarioActual);
    }

    public void abrirOrdenar() {
        List<String> opciones = new ArrayList<>();
        opciones.add("Orden de llegada");
        opciones.add("Empresas primero");
        opciones.add("Inversores primero");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Orden");
        dialog.setHeaderText("En qué orden se deben mostrar las solicitudes?");
        dialog.setContentText("Elegir opción:");

        Optional<String> input = dialog.showAndWait();
        input.ifPresent(opcion -> ordenElegido = opcion);
        dialog.close();
    }

    public void reordenar() {
        if (ordenElegido == null || ordenElegido.equals("Orden de llegada")) {
            //TODO
        } else if (ordenElegido.equals("Empresas primero")) {
            Collections.reverse(usuariosPendientes);
        }
    }
}
