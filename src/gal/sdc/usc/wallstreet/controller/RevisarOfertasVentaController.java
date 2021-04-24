package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RevisarOfertasVentaController extends DatabaseLinker {

    @FXML
    private Button btn_aceptar;
    @FXML
    private Button btn_rechazar;
    @FXML
    private Button btn_siguiente;
    @FXML
    private Button btn_anterior;
    @FXML
    private Label txt_fecha;
    @FXML
    private Label txt_empresa;
    @FXML
    private Label txt_usuario;
    @FXML
    private Label txt_num_part;
    @FXML
    private Label txt_precio_venta;
    @FXML
    private Button btn_ver_empresa;
    @FXML
    private Button btn_ver_usuario;


    public List<OfertaVenta> ofertasPendientes;
    public OfertaVenta ofertaActual;
    public String ordenElegido;

    public void initialize() {
        obtenerDatos();

        if (ofertasPendientes.size() > 1) abrirOrdenar();

        reordenar();
        ofertaActual = ofertasPendientes.get(0);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void obtenerDatos() {
        ofertasPendientes = super.getDAO(OfertaVentaDAO.class).getOfertasPendientes();
    }

    public void anterior() {
        ofertaActual = ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) - 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void siguiente() {
        ofertaActual = ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) + 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void aceptar(){}

    public void rechazar(){}

/*    public void aceptar() {
        super.getDAO(OfertaVentaDAO.class).aceptarOfertaVet(ofertaActual.getSuperUsuario().getIdentificador());
        //TODO: borrar usuario
        if (usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1){
            siguiente();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1));
        } else if (usuariosBajas.indexOf(usuarioActual) != 0){
            anterior();
            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1));
        } else {
            cerrarVentana();
        }
    }*/

    public void mostrarDatos() {
        txt_fecha.setText(ofertaActual.getFecha().toString());
        txt_empresa.setText(ofertaActual.getEmpresa().getNombre());
        txt_usuario.setText(ofertaActual.getUsuario().getIdentificador());
        txt_num_part.setText(ofertaActual.getNumParticipaciones().toString());
        txt_precio_venta.setText(ofertaActual.getPrecioVenta().toString());
    }

    public void controlarVisibilidadesAnteriorPosterior() {
        btn_siguiente.setVisible(ofertasPendientes.indexOf(ofertaActual) != ofertasPendientes.size() - 1);
        btn_anterior.setVisible(ofertasPendientes.indexOf(ofertaActual) != 0);
    }

    public void abrirOrdenar() {
        List<String> opciones = new ArrayList<>();
        opciones.add("Orden de creación");
        opciones.add("Número de participaciones");
        opciones.add("Precio de venta");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Orden");
        dialog.setHeaderText("¿En qué orden se deben mostrar las ofertas de venta?");
        dialog.setContentText("Elegir opción:");

        Optional<String> input = dialog.showAndWait();
        input.ifPresent(opcion -> ordenElegido = opcion);
        dialog.close();
    }

    public void reordenar(){
        if (ordenElegido == null || ordenElegido.equals("Orden de creación")){
            ofertasPendientes.sort(Comparator.comparing(OfertaVenta::getFecha));
        } else if (ordenElegido.equals("Número de participaciones")){
            ofertasPendientes.sort(Comparator.comparing(OfertaVenta::getNumParticipaciones));
        } else {
            ofertasPendientes.sort(Comparator.comparing(OfertaVenta::getPrecioVenta));
        }
    }
}
