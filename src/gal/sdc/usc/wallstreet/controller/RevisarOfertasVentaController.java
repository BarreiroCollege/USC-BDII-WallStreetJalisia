package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RevisarOfertasVentaController extends DatabaseLinker {

    @FXML
    public Button btn_aceptar;
    @FXML
    public Button btn_rechazar;
    @FXML
    public Button btn_siguiente;
    @FXML
    public Button btn_anterior;
    @FXML
    public Label txt_fecha;
    @FXML
    public Label txt_empresa;
    @FXML
    public Label txt_usuario;
    @FXML
    public Label txt_num_part;
    @FXML
    public Label txt_precio_venta;
    @FXML
    public Button btn_ver_empresa;
    @FXML
    public Button btn_ver_usuario;

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
        //TODO: ordenar
    }

    public void anterior() {
        ofertaActual = ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) - 1);
        mostrarDatos();
        //controlarVisibilidadesAnteriorPosterior();
    }

    public void siguiente() {
        ofertaActual = ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) + 1);
        mostrarDatos();
        //controlarVisibilidadesAnteriorPosterior();
    }

//    public void aceptar() {
//        super.getDAO(UsuarioDAO.class).aceptarUsuario(ofertaActual.getSuperUsuario().getIdentificador());
//        //TODO: borrar usuario
//        if (usuariosBajas.indexOf(usuarioActual) != usuariosBajas.size() - 1){
//            siguiente();
//            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) - 1));
//        } else if (usuariosBajas.indexOf(usuarioActual) != 0){
//            anterior();
//            usuariosBajas.remove(usuariosBajas.get(usuariosBajas.indexOf(usuarioActual) + 1));
//        } else {
//            cerrarVentana();
//        }
//    }

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

        ChoiceDialog<String> dialog = new ChoiceDialog<>();
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
