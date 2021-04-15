package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.validation.IntegerValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public class CarteraController extends DatabaseLinker {

    public static final String VIEW = "cartera";
    public static final Integer HEIGHT = 440;
    public static final Integer WIDTH = 610;

    // Tabla y columnas de la tabla
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
    private TableColumn<Participacion, String> cartera_tabla_pago;

    // Textos de saldo del usuario
    @FXML
    private Label txt_saldo;
    @FXML
    private Label txt_saldo_real;

    // Opciones de filtrado
    @FXML
    private JFXToggleButton toggle_filtro;
    @FXML
    private Pane cartera_filtro;
    @FXML
    private ComboBox<String> cb_empresa;
    @FXML
    private JFXTextField txt_min_part;
    @FXML
    private JFXTextField txt_max_part;
    @FXML
    private JFXTextField txt_min_part_bloq;
    @FXML
    private JFXTextField txt_max_part_bloq;
    @FXML
    private DatePicker datepck_despues_pago;
    @FXML
    private DatePicker datepck_antes_pago;

    private final ObservableList<Participacion> datosTabla = FXCollections.observableArrayList();
    private String cbTexto;
    private FilteredList<String> empresas;


    /**
     * Inicializa la tabla de datos que se muestra en Cartera
     * Establece los valores que buscar para cada columna de la tabla
     */
    @FXML
    public void initialize(){
        togglePanelFiltro();
        final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/L/y"); // Asigna el formato de fecha para la tabla

        // Establecemos los valores que contendrá cada columna
        cartera_tabla_empresa.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getNombre()));
        cartera_tabla_cif.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getCif()));
        cartera_tabla_cant.setCellValueFactory( new PropertyValueFactory<>("cantidad") );
        cartera_tabla_cant_bloq.setCellValueFactory( new PropertyValueFactory<>("cantidadBloqueada") );
        cartera_tabla_pago.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getFechaUltimoPago() == null?
                "Nunca" : celda.getValue().getEmpresa().getFechaUltimoPago().toLocalDateTime().toLocalDate().format(formatoFecha) ));
        cartera_tabla.setPlaceholder( new Label("No se encuentran participaciones") );
        // TODO: testear filtrado por fecha (meter valores de prueba) [Si ambos filtros de fecha son nulos no muestra todas las acciones]

        // Validador de entrada numérica
        IntegerValidator iv = new IntegerValidator("");
        txt_min_part.getValidators().add(iv);
        txt_max_part.getValidators().add(iv);
        txt_max_part_bloq.getValidators().add(iv);
        txt_min_part_bloq.getValidators().add(iv);

        txt_min_part.textProperty().addListener((observable, oldValue, newValue) -> { txt_min_part.validate(); });
        txt_max_part.textProperty().addListener((observable, oldValue, newValue) -> { txt_max_part.validate(); });
        txt_max_part_bloq.textProperty().addListener((observable, oldValue, newValue) -> { txt_max_part_bloq.validate(); });
        txt_min_part_bloq.textProperty().addListener((observable, oldValue, newValue) -> { txt_min_part_bloq.validate(); });

        // Indicamos a la tabla que sus contenidos serán los de la lista datosTabla
        actualizarDatos();
        cartera_tabla.setItems(datosTabla);

        // La ComboBox muestra los nombres de las empresas.
        for (Participacion part : datosTabla){
            cb_empresa.getItems().add(part.getEmpresa().getNombre());
        }
        empresas = cb_empresa.getItems().filtered(null); // Se guardan todas las empresas

        // La ComboBox es editable y actualiza sus opciones en función de lo escrito por el usuario.
        cb_empresa.valueProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<String> empresasFiltradas = empresas;
            empresasFiltradas.setPredicate(empresa -> {
                if (newValue == null || newValue.isEmpty()){
                    // Corrige bug cuando se intentan borrar caracteres
                    return true;
                }
                return empresa.toLowerCase().contains(newValue.toLowerCase());
            });
            cb_empresa.setItems(empresasFiltradas);
            cbTexto = newValue; // Se guarda el valor para un posible filtrado (botón)
        });
    }

    // Abre o cierra la ventana de filtros de la tabla de participaciones, además de redimensionar la tabla
    public void togglePanelFiltro(){
        if ( toggle_filtro.isSelected() ){
            cartera_filtro.setVisible(true);
            cartera_tabla.setPrefSize(265,263);
            cartera_tabla_empresa.setPrefWidth(91.3);
            cartera_tabla_cif.setPrefWidth(75);
            cartera_tabla_cant.setPrefWidth(91.2);
            cartera_tabla_cant_bloq.setPrefWidth(91.2);
            cartera_tabla_pago.setPrefWidth(91.2);
        } else {
            cartera_filtro.setVisible(false);
            cartera_tabla.setPrefSize(545,263);
            cartera_tabla_empresa.setPrefWidth(220);
            cartera_tabla_cif.setPrefWidth(84);
            cartera_tabla_cant.setPrefWidth(75);
            cartera_tabla_cant_bloq.setPrefWidth(75);
            cartera_tabla_pago.setPrefWidth(90);
        }
    }

    public void actualizarDatos() {

        // TODO OPERACIONES A TRAVÉS DEL USUARIO CON LOGIN
        Usuario usuario = super.getDAO(UsuarioDAO.class).getUsuario("iv12");

        // Accedemos al DAO de Participaciones para comprobar las participaciones que posea el usuario
        List<Participacion> participaciones = super.getDAO(ParticipacionDAO.class).getParticipaciones(usuario);

        // Introducimos los datos leidos de la bd a nuestra ObservableList
        datosTabla.setAll(participaciones);

        // Actualizamos el saldo del usuario consultado
        txt_saldo.setText( usuario.getSaldo()-usuario.getSaldoBloqueado() + " €");
        txt_saldo_real.setText( usuario.getSaldo() + " €");
    }

    /**
     * Comprueba que el campo de texto de entrada sólo contenga caracteres numéricos.
     * En caso contrario muestra una alerta de lo ocurrido.
     * @param entrada Campo de texto a verificar
     * @return resultado de la verificación, una vez el usuario haya cerrado la alerta en el caso de haberla
     */
    public boolean regexNumerico(JFXTextField entrada){
        // Solo se aceptan caracteres numéricos
        if(entrada.getText() != null && !entrada.getText().isEmpty()){
            if (!entrada.getText().matches("[0-9]+")){
                if ( datosTabla.isEmpty() ) Main.mensaje("Introduce un número válido de participaciones",3);
                return false;
            }
        }
        return true;
    }

    /**
     *  Filtra los datos de participaciones mostradas en la tabla en función de:
     *  - Empresa seleccionada en ComboBox
     *  - Mínimo número de participaciones no bloqueadas
     *  - Máximo número de participaciones no bloqueadas
     *  - Mínimo número de participaciones bloqueadas
     *  - Máximo número de participaciones bloqueadas
     *  - Límite inferior para la fecha del último pago de la empresa
     *  - Límite superior para la fecha del último pago de la empresa
     */
    public void filtrarDatos(){

        // Cambiamos el placeholder de la tabla para indicar que el filtro no obtuvo resultados
        cartera_tabla.setPlaceholder( new Label("No se encuentran participaciones con los parámetros indicados") );

        // Se guardan todas las participaciones en un FilteredList
        FilteredList<Participacion> partFiltradas = new FilteredList<>(datosTabla, p -> true);

        if ( !regexNumerico(txt_min_part) ) return;
        if ( !regexNumerico(txt_max_part) ) return;
        if ( !regexNumerico(txt_min_part_bloq) ) return;
        if ( !regexNumerico(txt_max_part_bloq) ) return;

        // Se eliminan aquellas participaciones no válidas
        Predicate<Participacion> predicadoTotal = construirPredicadosFiltro();
        partFiltradas.setPredicate(predicadoTotal);

        // Una FilteredList no se puede modificar. Se almacena como SortedList para que pueda ser ordenada.
        SortedList<Participacion> partOrdenadas = new SortedList<>(partFiltradas);

        // La ordenación de partOrdenadas sigue el criterio de la tabla.
        partOrdenadas.comparatorProperty().bind(cartera_tabla.comparatorProperty());

        // Se borra la antigua información de la tabla y se muestra la nueva.
        cartera_tabla.setItems(partOrdenadas);
    }

    private Predicate<Participacion> construirPredicadosFiltro(){

        // Predicado correspondiente a la ComboBox
        Predicate<Participacion> predComboBox = participacion -> {
            // Se comprueba si hay algún valor seleccionado o escrito en la ComboBox
            if (cb_empresa.getValue() != null && !cb_empresa.getValue().isEmpty()){
                // El nombre comercial de la empresa de la participación debe contener la selección
                return participacion.getEmpresa().getNombre().toLowerCase().contains(cbTexto.toLowerCase());
            }
            return true;
        };

        // Predicados correspondientes al rango de participaciones no bloqueadas
        Predicate<Participacion> predMinPart = participacion -> {
            if (txt_min_part.getText() != null && !txt_min_part.getText().isEmpty()){
                return participacion.getCantidad() >= Integer.parseInt(txt_min_part.getText());
            }
            return true;
        };

        Predicate<Participacion> predMaxPart= participacion -> {
            if (txt_max_part.getText() != null && !txt_max_part.getText().isEmpty()){
                return participacion.getCantidad() <= Integer.parseInt(txt_max_part.getText());
            }
            return true;
        };

        // Predicados correspondientes al rango de participaciones bloqueadas
        Predicate<Participacion> predMinPartBloq = participacion -> {
            if (txt_min_part_bloq.getText() != null && !txt_min_part_bloq.getText().isEmpty()){
                return participacion.getCantidadBloqueada() >= Integer.parseInt(txt_min_part_bloq.getText());
            }
            return true;
        };

        Predicate<Participacion> predMaxPartBloq = participacion -> {
            if (txt_max_part_bloq.getText() != null && !txt_max_part_bloq.getText().isEmpty()){
                return participacion.getCantidadBloqueada() <= Integer.parseInt(txt_max_part_bloq.getText());
            }
            return true;
        };

        // Predicados correspondientes al rango de fechas del último pago
        // Se filtra en función del último pago que realizó la empresa (que el usuario puede no haber recibido)
        Predicate<Participacion> predDespuesFecha = participacion -> {
            if (participacion.getEmpresa().getFechaUltimoPago() == null){
                return false;
            } else if (datepck_despues_pago.getValue() != null){
                return participacion.getEmpresa().getFechaUltimoPago()
                        .compareTo(java.sql.Date.valueOf(datepck_despues_pago.getValue())) >= 0;
            }
            return true;
        };

        Predicate<Participacion> predAntesFecha = participacion -> {
            if (participacion.getEmpresa().getFechaUltimoPago() == null){
                return false;
            } else if (datepck_antes_pago.getValue() != null){
                return participacion.getEmpresa().getFechaUltimoPago()
                        .compareTo(java.sql.Date.valueOf(datepck_antes_pago.getValue())) <= 0;
            }
            return true;
        };

        return predComboBox.and(predMinPart).and(predMaxPart).and(predMinPartBloq).and(predMaxPartBloq)
                .and(predDespuesFecha).and(predAntesFecha);
    }
}