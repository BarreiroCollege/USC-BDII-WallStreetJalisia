package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.validation.IntegerValidator;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public class CarteraController extends DatabaseLinker {

    // Parámetros de la ventana
    public static final String VIEW = "cartera";
    public static final Integer HEIGHT = 480;
    public static final Integer WIDTH = 897;
    public static final String TITULO = "Cartera";
    private final ObservableList<Participacion> datosTabla = FXCollections.observableArrayList();
    private final ObservableList<OfertaVenta> datosTablaOfertas = FXCollections.observableArrayList();
    // Opciones de filtrado en la tabla de ofertas de venta
    @FXML
    public JFXComboBox<String> cb_empresa_ofertas;
    //<editor-fold defaultstate="collapsed" desc="Variables desde FXML">
    // Tabla y columnas de la tabla de participaciones
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
    // Tabla y columnas de la tabla de ofertas de venta
    @FXML
    private TableView<OfertaVenta> cartera_tablaOferta;
    @FXML
    private TableColumn<OfertaVenta, String> cartera_tablaOferta_fecha;
    @FXML
    private TableColumn<OfertaVenta, String> cartera_tablaOferta_empresa;
    @FXML
    private TableColumn<OfertaVenta, String> cartera_tablaOferta_cif;
    @FXML
    private TableColumn<OfertaVenta, Integer> cartera_tablaOferta_cant;
    @FXML
    private TableColumn<OfertaVenta, Integer> cartera_tablaOferta_sin_vender;
    @FXML
    private TableColumn<OfertaVenta, String> cartera_tablaOferta_precio;
    // Textos de saldo del usuario
    @FXML
    private Label txt_saldo;
    @FXML
    private Label txt_saldo_real;
    @FXML
    private JFXTabPane menu_pestanas;
    // Opciones de filtrado de la tabla de participaciones
    @FXML
    private JFXToggleButton toggle_filtro;
    @FXML
    private Pane cartera_oferta_filtro;
    @FXML
    private Pane cartera_filtro;
    @FXML
    private JFXComboBox<String> cb_empresa;
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
    @FXML
    private JFXTextField txt_min_part_ofertas;
    @FXML
    private JFXTextField txt_max_part_ofertas;
    @FXML
    private JFXTextField txt_min_precio;
    @FXML
    private JFXTextField txt_max_precio;
    @FXML
    private DatePicker datepck_despues_oferta;
    @FXML
    private DatePicker datepck_antes_oferta;

    //</editor-fold>
    @FXML
    private CheckBox CheckOfertasActivas;
    // Botón para dar de baja una oferta de venta
    @FXML
    private JFXButton cartera_btn_dar_de_baja;
    private String cbTexto;         // Valor seleccionado actualmente en la ComboBox de la pestaña de participaciones
    private String cbTextoOfertas;  // Valor seleccionado actualmente en la ComboBox de la pestaña de ofertas de venta
    private FilteredList<String> empresas;
    private FilteredList<String> empresasOfertas;

    /**
     * Inicializa la tabla de datos que se muestra en Cartera
     * Establece los valores que buscar para cada columna de la tabla
     */
    @FXML
    public void initialize() {
        establecerColumnasTablas();

        // Placeholders de las tablas de datos
        cartera_tabla.setPlaceholder(new Label("No dispones de participaciones"));
        cartera_tablaOferta.setPlaceholder(new Label("No dispones de ninguna oferta de venta"));

        addValidadores();

        // Indicamos a la tabla que sus contenidos serán los de la lista datosTabla
        actualizarDatos();

        // Las ComboBox muestran los nombres de las empresas que les correspondan.
        datosTabla.forEach(part -> {
            if (!cb_empresa.getItems().contains(part.getEmpresa().getNombre()))
                cb_empresa.getItems().add(part.getEmpresa().getNombre());
        });
        datosTablaOfertas.forEach(oferta -> {
            if (!cb_empresa_ofertas.getItems().contains(oferta.getEmpresa().getNombre()))
                cb_empresa_ofertas.getItems().add(oferta.getEmpresa().getNombre());
        });
        // Se guardan todas las empresas de las que hay participaciones
        empresas = cb_empresa.getItems().filtered(null);
        empresasOfertas = cb_empresa_ofertas.getItems().filtered(null);

        addListeners();
    }

    // Volver al menu principal
    public void btnVolver() {
        Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
    }

    public void actualizarDatos() {
        Usuario usuario = super.getUsuarioSesion().getUsuario();

        // Accedemos a los DAOs para obtener los datos del usuario actual
        List<Participacion> participaciones = super.getDAO(ParticipacionDAO.class).getParticipaciones(usuario);
        List<OfertaVenta> ofertas = super.getDAO(OfertaVentaDAO.class).getOfertasVenta(usuario);

        // Introducimos los datos leidos de la bd a nuestra ObservableList
        datosTabla.setAll(participaciones);
        datosTablaOfertas.setAll(ofertas);
        // Actualizamos el saldo del usuario consultado
        txt_saldo.setText(usuario.getSaldo() - usuario.getSaldoBloqueado() + " €");
        txt_saldo_real.setText(usuario.getSaldo() + " €");

        cartera_tabla.setItems(datosTabla);
        cartera_tablaOferta.setItems(datosTablaOfertas);
    }

    /**
     * Comprueba que el campo de texto de entrada sólo contenga caracteres numéricos.
     * En caso contrario muestra una alerta de lo ocurrido.
     *
     * @param entrada Campo de texto a verificar
     * @return resultado de la verificación, una vez el usuario haya cerrado la alerta en el caso de haberla
     */
    public boolean regexNumerico(JFXTextField entrada) {
        // Solo se aceptan caracteres numéricos
        if (entrada.getText() != null && !entrada.getText().isEmpty()) {
            if (!entrada.getText().matches("[0-9]+")) {
                if (datosTabla.isEmpty()) Main.mensaje("Introduce un número válido de participaciones", 3);
                return false;
            }
        }
        return true;
    }

    /**
     * Comprueba qu el campo de texto de entrada sea un precio válido (números seguidos, opcionalmente, por un punto y
     * hasta 2 decimales).
     * En caso contrario muestra una alerta de lo ocurrido.
     *
     * @param entrada Campo de texto a verificar
     * @return resultado de la verificación, una vez el usuario haya cerrado la alerta en caso de haberla
     */
    public boolean regexPrecio(JFXTextField entrada) {
        // La entrada acepta uno o más números, que pueden ir seguidos de un punto y hasta 2 números decimales.
        if (entrada.getText() != null && !entrada.getText().isEmpty()) {
            if (!entrada.getText().matches("[0-9]+([.][0-9]{1,2})?")) {
                if (datosTabla.isEmpty()) Main.mensaje("Introduce un precio válido", 3);
                return false;
            }
        }
        return true;
    }

    /**
     * Filtra los datos mostradas en la tabla de participaciones en función de:
     * - Empresa seleccionada en ComboBox
     * - Mínimo número de participaciones no bloqueadas
     * - Máximo número de participaciones no bloqueadas
     * - Mínimo número de participaciones bloqueadas
     * - Máximo número de participaciones bloqueadas
     * - Límite inferior para la fecha del último pago de la empresa
     * - Límite superior para la fecha del último pago de la empresa
     */
    public void filtrarDatosParticipaciones() {
        // Cambiamos el placeholder de la tabla para indicar que el filtro no obtuvo resultados
        cartera_tabla.setPlaceholder(new Label("No se encuentran participaciones con los parámetros indicados"));

        // Se guardan todas las participaciones en un FilteredList
        FilteredList<Participacion> partFiltradas = new FilteredList<>(datosTabla, p -> true);

        if (!regexNumerico(txt_min_part)) return;
        if (!regexNumerico(txt_max_part)) return;
        if (!regexNumerico(txt_min_part_bloq)) return;
        if (!regexNumerico(txt_max_part_bloq)) return;

        // Se eliminan aquellas participaciones no válidas
        Predicate<Participacion> predicadoTotal = construirPredicadosFiltroParticipaciones();
        partFiltradas.setPredicate(predicadoTotal);

        // Una FilteredList no se puede modificar. Se almacena como SortedList para que pueda ser ordenada.
        SortedList<Participacion> partOrdenadas = new SortedList<>(partFiltradas);

        // La ordenación de partOrdenadas sigue el criterio de la tabla.
        partOrdenadas.comparatorProperty().bind(cartera_tabla.comparatorProperty());

        // Se borra la antigua información de la tabla y se muestra la nueva.
        cartera_tabla.setItems(partOrdenadas);
    }


    /**
     * Filtra los datos mostradas en la tabla de ofertas de venta en función de:
     * - Límite inferior para la fecha del último pago de la empresa
     * - Límite superior para la fecha del último pago de la empresa
     * - Empresa seleccionada en ComboBox
     * - Mínimo número de participaciones puestas en venta
     * - Máximo número de participaciones puestas en venta
     * - Mínimo precio de venta
     * - Máximo precio de venta
     */
    public void filtrarDatosOfertas() {
        // Cambiamos el placeholder de la tabla para indicar que el filtro no obtuvo resultados
        cartera_tablaOferta.setPlaceholder(new Label("No se encuentran ofertas con los parámetros indicados"));

        // Se guardan todas las ofertas de venta en un FilteredList
        FilteredList<OfertaVenta> partFiltradasOfertas = new FilteredList<>(datosTablaOfertas, p -> true);

        if (!regexNumerico(txt_min_part)) return;
        if (!regexNumerico(txt_max_part)) return;
        if (!regexPrecio(txt_min_precio)) return;
        if (!regexPrecio(txt_max_precio)) return;

        // Se eliminan aquellas ofertas de venta no válidas
        Predicate<OfertaVenta> predicadoTotalOfertas = construirPredicadosFiltroOfertas();
        partFiltradasOfertas.setPredicate(predicadoTotalOfertas);

        // Una FilteredList no se puede modificar. Se almacena como SortedList para que pueda ser ordenada.
        SortedList<OfertaVenta> partOrdenadasOfertas = new SortedList<>(partFiltradasOfertas);

        // La ordenación de partOrdenadas sigue el criterio de la tabla.
        partOrdenadasOfertas.comparatorProperty().bind(cartera_tablaOferta.comparatorProperty());

        // Se borra la antigua información de la tabla y se muestra la nueva.
        cartera_tablaOferta.setItems(partOrdenadasOfertas);
    }

    public void darDeBajaOferta() {
        // Se toma la oferta a dar de baja
        OfertaVenta oferta = cartera_tablaOferta.getSelectionModel().getSelectedItem();

        // Se borra la oferta de la base de datos
        super.iniciarTransaccion();

        // Se recuperan los datos de la cartera (participaciones) asociadas al usuario y a la empresa de la oferta
        Participacion saldo = super.getDAO(ParticipacionDAO.class).seleccionar(getUsuarioSesion().getUsuario().getSuperUsuario(), oferta.getEmpresa());
        // Las participaciones restantes se descuentan de la cantidad que tenía bloqueada
        saldo.setCantidadBloqueada(saldo.getCantidadBloqueada() - oferta.getRestantes());
        // Se guarda la información en la cartera
        super.getDAO(ParticipacionDAO.class).actualizar(saldo);
        // La oferta de venta queda como si se hubiera finalizado
        oferta.setRestantes(0);
        // Se guardan los cambios de la oferta
        super.getDAO(OfertaVentaDAO.class).actualizar(oferta);

        if (super.ejecutarTransaccion()) {
            Main.mensaje("Oferta de venta retirada");
        } else {
            Main.mensaje("Error; no se ha podido retirar la oferta");
        }

        // Se actualiza la ComboBox. No se puede eliminar la empresa directamente porque puede haber otras ofertas de la misma
        int i = 0;
        String empresaAEliminar = cartera_tablaOferta.getSelectionModel().getSelectedItem().getEmpresa().getNombre();
        for (OfertaVenta ofertaVenta : datosTablaOfertas) {
            if (ofertaVenta.getEmpresa().getNombre().equals(empresaAEliminar)) {
                i++;
                if (i == 2) {       // Si hay más de una oferta de la misma empresa, no será necesario borarla
                    break;
                }
            }
        }
        // Si la empresa solo tenía una oferta de venta asociada, se elimina de la ComboBox
        if (i == 1) cb_empresa_ofertas.getItems().remove(empresaAEliminar);

        // Se elimina la oferta de la tabla
        datosTablaOfertas.remove(cartera_tablaOferta.getSelectionModel().getSelectedItem());
    }

    private void establecerColumnasTablas() {
        final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/L/y"); // Asigna el formato de fecha para la tabla de participaciones
        final DateFormat formatoFechaTabla = new SimpleDateFormat("d/L/y");   // Asigna el formato de fecha para la tabla de ofertas de venta

        // Establecemos los valores que contendrá cada columna de la tabla de participaciones
        cartera_tabla_empresa.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getNombre()));
        cartera_tabla_cif.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getCif()));
        cartera_tabla_cant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        cartera_tabla_cant_bloq.setCellValueFactory(new PropertyValueFactory<>("cantidadBloqueada"));
        cartera_tabla_pago.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getFechaUltimoPago() == null ?
                "Nunca" : celda.getValue().getEmpresa().getFechaUltimoPago().toLocalDateTime().toLocalDate().format(formatoFecha)));

        // Establecemos los valores que contendrá cada columna de la tabla de ofertas de venta
        cartera_tablaOferta_fecha.setCellValueFactory(celda -> new SimpleStringProperty(formatoFechaTabla.format(celda.getValue().getFecha())));
        cartera_tablaOferta_empresa.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getNombre()));
        cartera_tablaOferta_cif.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getEmpresa().getCif()));
        cartera_tablaOferta_cant.setCellValueFactory(new PropertyValueFactory<>("numParticipaciones"));
        cartera_tablaOferta_sin_vender.setCellValueFactory(new PropertyValueFactory<>("restantes"));
        cartera_tablaOferta_precio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
    }

    private void addValidadores() {
        // Validadores de entrada numérica
        IntegerValidator iv = new IntegerValidator("");
        txt_min_part.getValidators().add(iv);
        txt_max_part.getValidators().add(iv);
        txt_max_part_bloq.getValidators().add(iv);
        txt_min_part_bloq.getValidators().add(iv);
        txt_min_part_ofertas.getValidators().add(iv);
        txt_max_part_ofertas.getValidators().add(iv);

        txt_min_part.textProperty().addListener((observable, oldValue, newValue) -> {
            txt_min_part.validate();
        });
        txt_max_part.textProperty().addListener((observable, oldValue, newValue) -> {
            txt_max_part.validate();
        });
        txt_max_part_bloq.textProperty().addListener((observable, oldValue, newValue) -> {
            txt_max_part_bloq.validate();
        });
        txt_min_part_bloq.textProperty().addListener((observable, oldValue, newValue) -> {
            txt_min_part_bloq.validate();
        });
        txt_min_part_ofertas.textProperty().addListener((observable, oldValue, newValue) -> {
            txt_min_part_ofertas.validate();
        });
        txt_max_part_ofertas.textProperty().addListener((observable, oldValue, newValue) -> {
            txt_max_part_ofertas.validate();
        });
    }

    private void addListeners() {
        // Las ComboBox son editables y actualizan sus opciones en función de lo escrito por el usuario.
        cb_empresa.valueProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<String> empresasFiltradas = empresas;
            empresasFiltradas.setPredicate(empresa -> {
                if (newValue == null || newValue.isEmpty()) {
                    // Corrige bug cuando se intentan borrar caracteres
                    return true;
                }
                return empresa.toLowerCase().contains(newValue.toLowerCase());
            });
            cb_empresa.setItems(empresasFiltradas);
            cbTexto = newValue; // Se guarda el valor para un posible filtrado (botón)
        });

        cb_empresa_ofertas.valueProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<String> empresasFiltradas = empresasOfertas;
            empresasFiltradas.setPredicate(empresa -> {
                if (newValue == null || newValue.isEmpty()) {
                    // Corrige bug cuando se intentan borrar caracteres
                    return true;
                }
                return empresa.toLowerCase().contains(newValue.toLowerCase());
            });
            cb_empresa_ofertas.setItems(empresasFiltradas);
            cbTextoOfertas = newValue; // Se guarda el valor para un posible filtrado (botón)
        });

        // Al cambiar de pestaña cambian algunos componentes.
        // En la pestaña de participaciones, no se muestra txt_min_precio ni txt_max_precio, pero sí
        // txt_min_part_bloq y txt_max_part_bloq. En la pestaña de ofertas de venta ocurre lo contrario.
        menu_pestanas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            cambiarVisibilidadTextos(menu_pestanas.getSelectionModel().isSelected(0));
        });

        // Cuando se selecciona una fila en la tabla de ofertas de venta, si la oferta de venta está activa, se puede dar de baja.
        cartera_tablaOferta.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldSelection, newSelection) -> {
                    if (newSelection.isOfertaActiva()) {
                        cartera_btn_dar_de_baja.setDisable(false);
                        return;
                    }
                    cartera_btn_dar_de_baja.setDisable(true);
                });
    }

    /**
     * Cambia la visibilidad de txt_min_part_bloq, txt_max_part_bloq, txt_min_precio y txt_max_precio en función de
     * la pestaña.
     *
     * @param pestaña true -> pestaña de participaciones. false -> pestaña de ofertas de venta
     */
    private void cambiarVisibilidadTextos(boolean pestaña) {
        txt_min_part_bloq.setVisible(pestaña);
        txt_max_part_bloq.setVisible(pestaña);
        txt_min_precio.setVisible(!pestaña);
        txt_max_precio.setVisible(!pestaña);
    }

    private Predicate<Participacion> construirPredicadosFiltroParticipaciones() {

        // Predicado correspondiente a la ComboBox
        Predicate<Participacion> predComboBox = participacion -> {
            // Se comprueba si hay algún valor seleccionado o escrito en la ComboBox
            if (cb_empresa.getValue() != null && !cb_empresa.getValue().isEmpty()) {
                // El nombre comercial de la empresa de la participación debe contener la selección
                return participacion.getEmpresa().getNombre().toLowerCase().contains(cbTexto.toLowerCase());
            }
            return true;
        };

        // Predicados correspondientes al rango de participaciones no bloqueadas
        Predicate<Participacion> predMinPart = participacion -> {
            if (txt_min_part.getText() != null && !txt_min_part.getText().isEmpty()) {
                return participacion.getCantidad() >= Integer.parseInt(txt_min_part.getText());
            }
            return true;
        };

        Predicate<Participacion> predMaxPart = participacion -> {
            if (txt_max_part.getText() != null && !txt_max_part.getText().isEmpty()) {
                return participacion.getCantidad() <= Integer.parseInt(txt_max_part.getText());
            }
            return true;
        };

        // Predicados correspondientes al rango de participaciones bloqueadas
        Predicate<Participacion> predMinPartBloq = participacion -> {
            if (txt_min_part_bloq.getText() != null && !txt_min_part_bloq.getText().isEmpty()) {
                return participacion.getCantidadBloqueada() >= Integer.parseInt(txt_min_part_bloq.getText());
            }
            return true;
        };

        Predicate<Participacion> predMaxPartBloq = participacion -> {
            if (txt_max_part_bloq.getText() != null && !txt_max_part_bloq.getText().isEmpty()) {
                return participacion.getCantidadBloqueada() <= Integer.parseInt(txt_max_part_bloq.getText());
            }
            return true;
        };

        // Predicados correspondientes al rango de fechas del último pago
        // Se filtra en función del último pago que realizó la empresa (que el usuario puede no haber recibido)
        Predicate<Participacion> predDespuesFecha = participacion -> {
            if (datepck_despues_pago.getValue() != null && !datepck_despues_pago.getValue().toString().isEmpty()) {
                if (participacion.getEmpresa().getFechaUltimoPago() == null) {
                    return false;
                }
                return participacion.getEmpresa().getFechaUltimoPago()
                        .compareTo(java.sql.Date.valueOf(datepck_despues_pago.getValue())) >= 0;
            }
            return true;
        };

        Predicate<Participacion> predAntesFecha = participacion -> {
            if (datepck_antes_pago.getValue() != null && !datepck_antes_pago.getValue().toString().isEmpty()) {
                if (participacion.getEmpresa().getFechaUltimoPago() == null) {
                    return false;
                }
                return participacion.getEmpresa().getFechaUltimoPago()
                        .compareTo(java.sql.Date.valueOf(datepck_antes_pago.getValue())) <= 0;
            }
            return true;
        };

        return predComboBox.and(predMinPart).and(predMaxPart).and(predMinPartBloq).and(predMaxPartBloq)
                .and(predDespuesFecha).and(predAntesFecha);
    }

    private Predicate<OfertaVenta> construirPredicadosFiltroOfertas() {

        // Predicados correspondientes al rango de fechas de la oferta de venta
        Predicate<OfertaVenta> predDespuesFecha = ofertaVenta -> {
            if (datepck_despues_oferta.getValue() != null && !datepck_despues_oferta.getValue().toString().isEmpty()) {
                return ofertaVenta.getFecha().after(Date.valueOf(datepck_despues_oferta.getValue()));
            }
            return true;
        };

        Predicate<OfertaVenta> predAntesFecha = ofertaVenta -> {
            if (datepck_antes_oferta.getValue() != null && !datepck_antes_oferta.getValue().toString().isEmpty()) {
                return ofertaVenta.getFecha().before(Date.valueOf(datepck_antes_oferta.getValue()));
            }
            return true;
        };

        // Predicado correspondiente a la ComboBox
        Predicate<OfertaVenta> predComboBox = ofertaVenta -> {
            // Se comprueba si hay algún valor seleccionado o escrito en la ComboBox
            if (cb_empresa_ofertas.getValue() != null && !cb_empresa_ofertas.getValue().isEmpty()) {
                // El nombre comercial de la empresa de la participación debe contener la selección
                return ofertaVenta.getEmpresa().getNombre().toLowerCase().contains(cbTextoOfertas.toLowerCase());
            }
            return true;
        };

        // Predicados correspondientes al rango de participaciones
        Predicate<OfertaVenta> predMinPart = ofertaVenta -> {
            if (txt_min_part_ofertas.getText() != null && !txt_min_part_ofertas.getText().isEmpty()) {
                return ofertaVenta.getNumParticipaciones() >= Integer.parseInt(txt_min_part_ofertas.getText());
            }
            return true;
        };

        Predicate<OfertaVenta> predMaxPart = ofertaVenta -> {
            if (txt_max_part_ofertas.getText() != null && !txt_max_part_ofertas.getText().isEmpty()) {
                return ofertaVenta.getNumParticipaciones() <= Integer.parseInt(txt_max_part_ofertas.getText());
            }
            return true;
        };

        // Predicados correspondientes al rango del precio de venta
        Predicate<OfertaVenta> predMinPrecio = ofertaVenta -> {
            if (txt_min_precio.getText() != null && !txt_min_precio.getText().isEmpty()) {
                return ofertaVenta.getPrecioVenta() >= Float.parseFloat(txt_min_precio.getText());
            }
            return true;
        };

        Predicate<OfertaVenta> predMaxPrecio = ofertaVenta -> {
            if (txt_max_precio.getText() != null && !txt_max_precio.getText().isEmpty()) {
                return ofertaVenta.getPrecioVenta() <= Float.parseFloat(txt_max_precio.getText());
            }
            return true;
        };

        Predicate<OfertaVenta> predOfertaActiva = ofertaVenta -> {
            if (CheckOfertasActivas.isSelected()) {
                return ofertaVenta.isOfertaActiva();
            }
            return true;
        };

        return predDespuesFecha.and(predAntesFecha).and(predComboBox).and(predMinPart).and(predMaxPart)
                .and(predMinPrecio).and(predMaxPrecio).and(predOfertaActiva);
    }

}
