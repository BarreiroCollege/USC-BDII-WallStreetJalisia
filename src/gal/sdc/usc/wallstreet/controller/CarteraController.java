package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.List;

public class CarteraController extends DatabaseLinker {
    private final ObservableList<Participacion> datosTabla = FXCollections.observableArrayList();
    @FXML
    private TableView<Participacion> cartera_tabla;
    @FXML
    private TableColumn<Participacion, String> cartera_tabla_empresa;
    @FXML
    private TableColumn<Participacion, Integer> cartera_tabla_cant;
    @FXML
    private TableColumn<Participacion, Integer> cartera_tabla_cant_bloq;
    @FXML
    private Text txt_saldo;

    /**
     * Inicializa la tabla de datos que se muestra en Cartera
     * Establece los valores que buscar para cada columna de la tabla
     */
    @FXML
    public void initialize(){
        // Establecemos los valores que contendrá cada columna
        cartera_tabla_empresa.setCellValueFactory( celda -> new SimpleStringProperty( celda.getValue().getEmpresa().getUsuario().getIdentificador() ) );
        cartera_tabla_cant.setCellValueFactory( new PropertyValueFactory<>("cantidad") );
        cartera_tabla_cant_bloq.setCellValueFactory( new PropertyValueFactory<>("cantidadBloqueada") );
        // Indicamos a la tabla que sus contenidos serán los de la lista datosTabla
        cartera_tabla.setItems(datosTabla);
        actualizarDatos();
    }

    public void actualizarDatos() {

        String idUsuario = "iv12"; /* TODO ELIMINAR ESTE PARÁMETRO TEMPORAL DE DEBUG */

        // Accedemos al DAO de Participaciones para comprobar las del usuario
        // TODO Mostrar datos para el usuario con la sesión actual
        List<Participacion> participaciones = super.getDAO(ParticipacionDAO.class).getParticipaciones(idUsuario);

        // Limpiamos los datos de la tabla e insertamos los que acabamos de obtener
        datosTabla.clear();
        datosTabla.addAll(participaciones);
        txt_saldo.setText( super.getDAO(UsuarioDAO.class).getUsuario(idUsuario).getSaldo().toString() + " €");
    }

}
