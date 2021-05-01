package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.SuperUsuarioDAO;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class RevisarRegistrosController extends DatabaseLinker {

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

    public List<Usuario> usuariosPendientes = new ArrayList<>();
    public HashMap<String, Empresa> empresasPendientes = new HashMap<>();      // identificador -> empresa
    public HashMap<String, Inversor> inversoresPendientes = new HashMap<>();   // identificador -> inversor
    public Usuario usuarioActual;
    public String ordenElegido;

    public void initialize() {
        obtenerDatos();             // Se buacan datos de las solicitudes de registro

        // Si hay más de una solicitud, se da opción a ordenarlas
        if (usuariosPendientes.size() > 1) abrirOrdenar();

        reordenar();
        usuarioActual = usuariosPendientes.get(0);
        mostrarDatos();             // Se muestran los datos del usuario que aparece primero en la lista
        controlarVisibilidadesAnteriorPosterior();
    }

    private void obtenerDatos() {
        /* Los usuarios quedan ordenados en una lista. Además, para cada usuario se guarda el correspondiente
         * inversor o empresa en un HashMap, permitiendo acceso inmediato a partir de identificador.
         */
        super.getDAO(InversorDAO.class).getInversoresRegistrosPendientes().forEach(inversor -> {
            usuariosPendientes.add(inversor.getUsuario());
            inversoresPendientes.put(inversor.getUsuario().getSuperUsuario().getIdentificador(), inversor);
        });
        super.getDAO(EmpresaDAO.class).getEmpresasRegistrosPendientes().forEach(empresa -> {
            usuariosPendientes.add(empresa.getUsuario());
            empresasPendientes.put(empresa.getUsuario().getSuperUsuario().getIdentificador(), empresa);
        });
    }

    private void mostrarDatos() {
        txt_id.setText(usuarioActual.getSuperUsuario().getIdentificador());
        txt_direccion.setText(usuarioActual.getDireccion() == null ? "N/A" : usuarioActual.getDireccion());
        txt_cp.setText(usuarioActual.getCp() == null ? "N/A" : usuarioActual.getCp());
        txt_localidad.setText(usuarioActual.getLocalidad() == null ? "N/A" : usuarioActual.getLocalidad());
        txt_tlf.setText(usuarioActual.getTelefono() == null ? "N/A" : usuarioActual.getTelefono().toString());

        // usuariosPendientes es suma directa de empresas e inversores pendientes -> en un conjunto hay una key
        if (inversoresPendientes.containsKey(usuarioActual.getSuperUsuario().getIdentificador())) {
            Inversor inversor = inversoresPendientes.get(usuarioActual.getSuperUsuario().getIdentificador());
            txt_subusuario.setText("Inversor");
            txt_titulo_id_subclase.setText("DNI:");
            txt_id_subclase.setText(inversor.getDni());
            txt_nombre.setText(inversor.getNombre());
            txt_apellidos.setText(inversor.getApellidos());
            txt_apellidos.setVisible(true);
        } else {
            Empresa empresa = empresasPendientes.get(usuarioActual.getSuperUsuario().getIdentificador());
            txt_subusuario.setText("Empresa");
            txt_titulo_id_subclase.setText("CIF:");
            txt_id_subclase.setText(empresa.getCif());
            txt_nombre.setText(empresa.getNombre());
            txt_apellidos.setVisible(false);
        }
    }

    // Botón de avance
    public void anterior() {
        usuarioActual = usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) - 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    // Botón de retroceso
    public void siguiente() {
        usuarioActual = usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) + 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void controlarVisibilidadesAnteriorPosterior() {
        btn_siguiente.setVisible(usuariosPendientes.indexOf(usuarioActual) != usuariosPendientes.size() - 1);
        btn_anterior.setVisible(usuariosPendientes.indexOf(usuarioActual) != 0);
    }

    public void cerrarVentana() {
        Stage stage = (Stage) btn_siguiente.getScene().getWindow();
        stage.close();
    }

    public void aceptar() {
        // Dado que un usuario no puede realizar ninguna acción hasta ser aceptada su solicitud, no va a haber
        // conflictos, y se puede utilizar un nivel de lecturas no comprometidas para acelerar la ejecución.
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(UsuarioDAO.class).aceptarUsuario(usuarioActual.getSuperUsuario().getIdentificador());
        super.ejecutarTransaccion();

        // Se deja de mostrar la solicitud y se determina qué botones deben ser visibles
        if (usuariosPendientes.indexOf(usuarioActual) != usuariosPendientes.size() - 1) {
            siguiente();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) - 1));
            controlarVisibilidadesAnteriorPosterior();
        } else if (usuariosPendientes.indexOf(usuarioActual) != 0) {
            anterior();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) + 1));
            controlarVisibilidadesAnteriorPosterior();
        } else {
            Main.aviso("No quedan registros pendientes");
            cerrarVentana();
        }
    }

    public void rechazar() {
        // Dado que un usuario no puede realizar ninguna acción hasta ser aceptada su solicitud, no va a haber
        // conflictos, y se puede utilizar un nivel de lecturas no comprometidas para acelerar la ejecución.
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(UsuarioDAO.class).rechazarSolicitud(usuarioActual.getSuperUsuario().getIdentificador());
        super.ejecutarTransaccion();

        // Se deja de mostrar la solicitud y se determina qué botones deben ser visibles
        if (usuariosPendientes.indexOf(usuarioActual) != usuariosPendientes.size() - 1) {
            siguiente();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) - 1));
            controlarVisibilidadesAnteriorPosterior();
        } else if (usuariosPendientes.indexOf(usuarioActual) != 0) {
            anterior();
            usuariosPendientes.remove(usuariosPendientes.get(usuariosPendientes.indexOf(usuarioActual) + 1));
            controlarVisibilidadesAnteriorPosterior();
        } else {
            Main.aviso("No quedan registros pendientes");
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
            // Se muestran en orden de solicitud
            usuariosPendientes.sort(Comparator.comparing(Usuario::getAlta));
        } else if (ordenElegido.equals("Empresas primero")) {
            Collections.reverse(usuariosPendientes);
        }
        // Por defecto -> inversores primero
    }
}
