package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RevisarRegistrosController extends DatabaseLinker {

    private static JFXSnackbar snackbar;
    @FXML
    private AnchorPane anchorPane;
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
    private List<Usuario> usuariosPendientes = new ArrayList<>();
    private HashMap<String, Empresa> empresasPendientes = new HashMap<>();      // identificador -> empresa
    private HashMap<String, Inversor> inversoresPendientes = new HashMap<>();   // identificador -> inversor
    private Usuario usuarioActual;
    private String ordenElegido;

    public static void mensaje(String mensaje) {
        mensaje(mensaje, null);
    }

    public static void mensaje(String mensaje, Integer duracion) {
        mensaje(new JFXSnackbarLayout(mensaje), duracion);
    }

    private static void mensaje(JFXSnackbarLayout layout, Integer duracion) {
        JFXSnackbarLayout finalLayout = new JFXSnackbarLayout(layout.getToast(), "Cerrar", e -> snackbar.close());
        if (duracion != null) {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout, Duration.seconds(duracion)));
        } else {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout));
        }
    }

    public void initialize() {
        obtenerDatos();             // Se buacan datos de las solicitudes de registro

        // Si hay más de una solicitud, se da opción a ordenarlas
        if (usuariosPendientes.size() > 1) abrirOrdenar();

        reordenar();
        usuarioActual = usuariosPendientes.get(0);
        mostrarDatos();             // Se muestran los datos del usuario que aparece primero en la lista
        controlarVisibilidadesAnteriorPosterior();

        // Se registra la snackbar
        snackbar = new JFXSnackbar(anchorPane);
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
        if (super.ejecutarTransaccion()) mensaje("Solicitud aceptada correctamente", 3);
        else {
            mensaje("Error al aceptar la solicitud", 3);
            return;
        }

        cambiarUsuario();
    }

    public void rechazar() {
        // Dado que un usuario no puede realizar ninguna acción hasta ser aceptada su solicitud, no va a haber
        // conflictos, y se puede utilizar un nivel de lecturas no comprometidas para acelerar la ejecución.
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(UsuarioDAO.class).rechazarSolicitud(usuarioActual.getSuperUsuario().getIdentificador());
        if (super.ejecutarTransaccion()) mensaje("Solicitud rechazada correctamente", 3);
        else {
            mensaje("Error al aceptar la solicitud", 3);
            return;
        }

        cambiarUsuario();
    }

    /***
     * Cambia de usuario. El actual se elimina y se pasa al siguiente, si lo hay. Si no, se retrocede al anterior.
     * Si este era el último usuario, se cierra la ventana.
     */
    public void cambiarUsuario() {
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
