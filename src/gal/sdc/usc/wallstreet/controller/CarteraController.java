package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private TableColumn<Participacion, String> cartera_tabla_cif;
    @FXML
    private TableColumn<Participacion, Integer> cartera_tabla_cant;
    @FXML
    private TableColumn<Participacion, Integer> cartera_tabla_cant_bloq;
    @FXML
    private Text txt_saldo;
    @FXML
    private ComboBox<String> cb_empresa;
    @FXML
    private Button cartera_btn_filtrar;

    private String cbTexto;

    /**
     * Inicializa la tabla de datos que se muestra en Cartera
     * Establece los valores que buscar para cada columna de la tabla
     */
    @FXML
    public void initialize(){
        // Establecemos los valores que contendrá cada columna
        cartera_tabla_empresa.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getNombre()));
        cartera_tabla_cif.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getCif()));
        cartera_tabla_cant.setCellValueFactory( new PropertyValueFactory<>("cantidad") );
        cartera_tabla_cant_bloq.setCellValueFactory( new PropertyValueFactory<>("cantidadBloqueada") );
        // Indicamos a la tabla que sus contenidos serán los de la lista datosTabla
        cartera_tabla.setItems(datosTabla);
        actualizarDatos();

        // La ComboBox muestra los nombres de las empresas.
        for (Participacion part : datosTabla){
            cb_empresa.getItems().add(part.getEmpresa().getNombre());
        }

        // La ComboBox es editable. Se define un listener para guardar el valor escrito/seleccionado.
        //TODO: filtrar opciones según el texto escrito
        cb_empresa.valueProperty().addListener((observable, oldValue, newValue) -> cbTexto = newValue);
    }

    public void actualizarDatos() {

        String idUsuario = "Xia"; /* TODO ELIMINAR ESTE PARÁMETRO TEMPORAL DE DEBUG */

        // Accedemos al DAO de Participaciones para comprobar las del usuario
        // TODO Mostrar datos para el usuario con la sesión actual
        List<Participacion> participaciones = super.getDAO(ParticipacionDAO.class).getParticipaciones(idUsuario);

        // Limpiamos los datos de la tabla e insertamos los que acabamos de obtener
        datosTabla.clear();
        datosTabla.addAll(participaciones);
        //txt_saldo.setText(super.getDAO(UsuarioDAO.class).getUsuario(idUsuario).getSaldo().toString() + " €");
    }

    /**
     *  Filtra los datos de participaciones mostradas en la tabla en función de:
     *  - Empresa seleccionada en ComboBox
     *  -
     */
    public void filtrarDatos(){
        // Se guardan todas las participaciones en un FilteredList
        FilteredList<Participacion> partFiltradas = new FilteredList<>(datosTabla, p -> true);

        // Se realiza el filtrado
        partFiltradas.setPredicate(participacion -> {
            // Se comprueba si hay algún valor seleccionado o escrito en la ComboBox
            if (cb_empresa.getValue() != null && !cb_empresa.getValue().isEmpty()){
                // El nombre comercial de la empresa de la participación debe contener la selección
                if (!participacion.getEmpresa().getNombre().toLowerCase().contains(cbTexto.toLowerCase())){
                    return false;
                }
            }
            return true;
        });

        // Una FilteredList no se puede modificar. Se almacena como SortedList para que pueda ser ordenada.
        SortedList<Participacion> partOrdenadas = new SortedList<>(partFiltradas);

        // La ordenación de partOrdenadas sigue el criterio de la tabla.
        partOrdenadas.comparatorProperty().bind(cartera_tabla.comparatorProperty());

        // Se borra la antigua información de la tabla y se muestra la nueva.
        cartera_tabla.setItems(partOrdenadas);
    }

}
