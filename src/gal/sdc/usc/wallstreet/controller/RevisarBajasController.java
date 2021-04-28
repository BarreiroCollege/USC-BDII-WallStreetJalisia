package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.*;
import gal.sdc.usc.wallstreet.repository.*;
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
import java.sql.Connection;
import java.util.*;

public class RevisarBajasController extends DatabaseLinker {

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

    public List<Usuario> usuariosBajas = new ArrayList<>();
    public HashMap<String, Empresa> empresasBajas = new HashMap<>();      // identificador -> empresa
    public HashMap<String, Inversor> inversoresBajas = new HashMap<>();   // identificador -> inversor
    public Usuario usuarioActual;
    public String ordenElegido;

    @FXML
    public void initialize() {
        obtenerDatos();         // Se buscan los datos de usuarios con solicitud de baja

        // Si hay más de una solicitud, se da opción a elegir el orden en el que se muestran
        if (usuariosBajas.size() > 1) abrirOrdenar();

        reordenar();
        usuarioActual = usuariosBajas.get(0);       // Usuario del que se van a mostrar datos
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void obtenerDatos() {
        /* Los usuarios quedan ordenados en una lista. Además, para cada usuario se guarda el correspondiente
         * inversor o empresa en un HashMap, permitiendo acceso inmediato a partir de identificador.
         */
        super.getDAO(InversorDAO.class).getInversoresBajasPendientes().forEach(inversor -> {
            usuariosBajas.add(inversor.getUsuario());
            inversoresBajas.put(inversor.getUsuario().getSuperUsuario().getIdentificador(), inversor);
        });
        super.getDAO(EmpresaDAO.class).getEmpresasBajasPendientes().forEach(empresa -> {
            usuariosBajas.add(empresa.getUsuario());
            empresasBajas.put(empresa.getUsuario().getSuperUsuario().getIdentificador(), empresa);
        });
    }

    public void mostrarDatos() {
        txt_id.setText(usuarioActual.getSuperUsuario().getIdentificador());
        txt_direccion.setText(usuarioActual.getDireccion() == null ? "N/A" : usuarioActual.getDireccion());
        txt_cp.setText(usuarioActual.getCp() == null ? "N/A" : usuarioActual.getCp());
        txt_localidad.setText(usuarioActual.getLocalidad() == null ? "N/A" : usuarioActual.getLocalidad());
        txt_tlf.setText(usuarioActual.getTelefono() == null ? "N/A" : usuarioActual.getTelefono().toString());

        // Jerarquía total -> en alguno de los dos HashMaps tiene que haber una key
        if (inversoresBajas.containsKey(usuarioActual.getSuperUsuario().getIdentificador())) {
            Inversor inversor = inversoresBajas.get(usuarioActual.getSuperUsuario().getIdentificador());
            txt_subusuario.setText("Inversor");
            txt_titulo_id_subclase.setText("DNI:");
            txt_id_subclase.setText(inversor.getDni());
            txt_nombre.setText(inversor.getNombre());
            txt_apellidos.setText(inversor.getApellidos());
            txt_apellidos.setVisible(true);
        } else {
            Empresa empresa = empresasBajas.get(usuarioActual.getSuperUsuario().getIdentificador());
            txt_subusuario.setText("Empresa");
            txt_titulo_id_subclase.setText("CIF:");
            txt_id_subclase.setText(empresa.getCif());
            txt_nombre.setText(empresa.getNombre());
            txt_apellidos.setVisible(false);
        }
    }

    // Botón de retroceso
    public void anterior() {
        usuarioActual = usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    // Botón de avance
    public void siguiente() {
        usuarioActual = usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    /**
     * Se acepta la baja del usuario mostrado.
     */
    public void aceptar() {
        /***
         * Para evitar que se produzcan movimientos en la cuenta del usuario una vez el regulador ha aceptado la baja
         * (pues el usuario aún podría comprar participaciones), se fuerza a una ejecución secuencial.
         */
        super.iniciarTransaccion(Connection.TRANSACTION_SERIALIZABLE);

        if (super.getDAO(ParticipacionDAO.class).tieneParticipaciones(usuarioActual)){
            // Si el usuario tiene participaciones (poseedor o como empresa), se cancela la solicitud de baja.
            super.getDAO(UsuarioDAO.class).rechazarBaja(usuarioActual.getSuperUsuario().getIdentificador());
            super.ejecutarTransaccion();
            // El regulador recibe una notificación.
            Main.aviso("El usuario tenía participaciones. La baja ha sido rechazada.");
            return;
        }

        // El usuario no tenía participaciones. Se retiran los fondos de su cuenta.
        super.getDAO(UsuarioDAO.class).vaciarSaldo(usuarioActual);

        // Se elimina la referencia
        super.getDAO(SuperUsuarioDAO.class).eliminarSuperUsuario(usuarioActual.getSuperUsuario().getIdentificador());
        super.ejecutarTransaccion();

        // Se actualizan los datos guardados y se determina qué botones mostrar.
        if (usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1) {
            siguiente();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1));
        } else if (usuariosBajas.indexOf(usuarioActual) != 0) {
            anterior();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1));
        } else {
            try {
                Main.aviso("No quedan usuarios con bajas pendientes");
                cerrarVentana();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cerrarVentana() {
        Stage stage = (Stage) btn_siguiente.getScene().getWindow();
        stage.close();
    }

    /**
     * Rechaza la baja del usuario mostrado.
     */
    public void rechazar() {
        super.getDAO(UsuarioDAO.class).rechazarBaja(usuarioActual.getSuperUsuario().getIdentificador());
        if (usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1) {
            siguiente();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1));
        } else if (usuariosBajas.indexOf(usuarioActual) != 0) {
            anterior();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1));
        } else {
            Main.aviso("No quedan usuarios con bajas pendientes");
            cerrarVentana();
        }
    }

    /**
     * Dependiendo de la posición de la baja que se está mostrando dentro de la lista de bajas, se permitirá avanzar,
     * retroceder, ambos o ninguno.
     */
    public void controlarVisibilidadesAnteriorPosterior() {
        btn_siguiente.setVisible(usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1);
        btn_anterior.setVisible(usuariosBajas.indexOf(usuarioActual) != 0);
    }

    public void abrirOrdenar() {
        // Opciones de orden.
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
            // Se toman en función de la fecha en la que solicitaron la baja
            usuariosBajas.sort(Comparator.comparing(Usuario::getBaja));
        } else if (ordenElegido.equals("Empresas primero")) {
            usuariosBajas.sort(Collections.reverseOrder());
        }
        // Por defecto -> inversores primero.
    }
}
