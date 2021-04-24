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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.*;

public class RevisarBajasController extends DatabaseLinker {

    @FXML
    private Button btn_aceptar;
    @FXML
    private Button btn_rechazar;
    @FXML
    private Button btn_siguiente;
    @FXML
    private Button btn_anterior;
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
    @FXML
    private Label txt_subusuario;
    @FXML
    private Label txt_titulo_id_subclase;
    @FXML
    private Label txt_id_subclase;
    @FXML
    private Label txt_nombre;
    @FXML
    private Label txt_apellidos;

    public List<Usuario> usuariosBajas;
    public Usuario usuarioActual;
    public String ordenElegido;

    @FXML
    public void initialize() {
        obtenerDatos();

        if (usuariosBajas.size() > 2) abrirOrdenar();

        reordenar();
        usuarioActual = usuariosBajas.get(0);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void obtenerDatos() {
        usuariosBajas = super.getDAO(InversorDAO.class).getInversoresBajasPendientes();
        usuariosBajas.addAll(super.getDAO(EmpresaDAO.class).getEmpresasBajasPendientes());
        // TODO: ordenar
    }

    public void mostrarDatos() {
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

    public void anterior() {
        usuarioActual = usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void siguiente() {
        usuarioActual = usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void aceptar() {
        super.getDAO(UsuarioDAO.class).aceptarUsuario(usuarioActual.getSuperUsuario().getIdentificador());
        //TODO: borrar usuario
        if (usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1) {
            siguiente();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1));
        } else if (usuariosBajas.indexOf(usuarioActual) != 0) {
            anterior();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1));
        } else {
            try {
                cerrarVentana();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void controlarVisibilidadesAnteriorPosterior() {
        btn_siguiente.setVisible(usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1);
        btn_anterior.setVisible(usuariosBajas.indexOf(usuarioActual) != 0);
    }

    public void cerrarVentana() {
        Stage stage = (Stage) btn_siguiente.getScene().getWindow();
        stage.close();
    }

    public void rechazar() {
        super.getDAO(UsuarioDAO.class).rechazarBaja(usuarioActual.getSuperUsuario().getIdentificador());
        if (usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1) {
            siguiente();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1));
        } else if (usuariosBajas.indexOf(usuarioActual) != 0) {
            anterior();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1));
        } else {
            cerrarVentana();
        }
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
            usuariosBajas.sort(Collections.reverseOrder());
        }
    }
}
