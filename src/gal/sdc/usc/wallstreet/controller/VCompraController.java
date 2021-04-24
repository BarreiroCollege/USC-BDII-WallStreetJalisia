package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Venta;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.VentaDAO;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Iconos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class VCompraController extends DatabaseLinker {
    public static final String VIEW = "VCompra";
    public static final Integer HEIGHT = 425;
    public static final Integer WIDTH = 760;
    public static final String TITULO = "Compra de participaciones";

    @FXML
    private JFXButton btnSalir;
    @FXML
    private JFXTextField campoNumero;
    @FXML
    private JFXTextField campoSaldo;
    @FXML
    private JFXComboBox<String> empresaComboBox;
    @FXML
    private JFXTextField campoPrecio;
    @FXML
    private TableView<OfertaVenta> tablaOfertas;
    @FXML
    private TableColumn<OfertaVenta, String> nombreCol;
    @FXML
    private TableColumn<OfertaVenta, Float> precioCol;
    @FXML
    private TableColumn<OfertaVenta, Date> fechaCol;
    @FXML
    private TableColumn<OfertaVenta, Integer> cantidadCol;
    @FXML
    private JFXButton botonRefresh;
    @FXML
    private JFXSnackbar notificationBar;

    private Usuario usr;
    private List<Empresa> listaEmpresas;
    private ObservableList<OfertaVenta> datosTabla;

    @FXML
    public void initialize() {

        listaEmpresas = new ArrayList<>();
        datosTabla = FXCollections.observableArrayList();

        // Recuperamos el usuario
       /* if(super.getTipoUsuario().equals(TipoUsuario.EMPRESA)) super.getEmpresa().getUsuario();
        else super.getInversor().getUsuario();*/
        usr = getDAO(UsuarioDAO.class).getUsuario("eva");

        // Setup de las columnas de la tabla
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cantidadCol.setCellValueFactory(new PropertyValueFactory<>("numParticipaciones"));
        tablaOfertas.setItems(datosTabla);
        tablaOfertas.setSelectionModel(null); // Evitamos que se seleccionen filas (estético)
        tablaOfertas.setPlaceholder(new Label("")); // Eliminamos el texto por defecto (estético)
        nombreCol.setCellValueFactory(p -> {
            if (p.getValue() != null) {
                return new SimpleStringProperty(p.getValue().getUsuario().getIdentificador());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });

        // Formato Integer para campoNumero y Float para campoPrecio
        campoNumero.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter(),
                null,
                c -> Pattern.matches("\\d*", c.getText()) ? c : null));
        campoPrecio.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> Pattern.compile("\\d*|\\d+\\.\\d{0,2}").matcher(change.getControlNewText()).matches() ? change : null));

        // Cuando se cambie el precio se actualizan las ofertas en base al nuevo
        campoPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
                actualizarDatosTabla();
        });

        // Cargamos saldo y preparamos botones de refresh
        botonRefresh.setGraphic(Iconos.icono(FontAwesomeIcon.REFRESH, "1em"));

        actualizarVentana();
    }

    // FUNCIONALIDADES //

    public void actualizarSaldo() {
        usr = getDAO(UsuarioDAO.class).getUsuario(usr.getSuperUsuario().getIdentificador());
        campoSaldo.setText(String.valueOf(usr.getSaldo()-usr.getSaldoBloqueado()));
    }

    public void actualizarListaEmpresas(){
        // Se carga la nueva lista
        listaEmpresas = getDAO(EmpresaDAO.class).getEmpresas();
        // Se limpia la comboBox y se vuelve a llenar
        empresaComboBox.getItems().clear();
        for (Empresa e : listaEmpresas) {
            empresaComboBox.getItems().add(e.getNombre() + " || " + e.getCif());
        }
    }

    public void actualizarDatosTabla() {
        // Si falta alguno de los campos necesarios, se limpia la tabla
        if (campoPrecio.getText().isEmpty() || empresaComboBox.getSelectionModel().getSelectedIndex() == -1) {
            datosTabla.clear();
            return;
        }
        String identificador = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getUsuario().getSuperUsuario().getIdentificador();
        datosTabla.setAll(getDAO(OfertaVentaDAO.class).getOfertasVenta(identificador, Float.parseFloat(campoPrecio.getText())));
    }

    public void actualizarVentana(){
        actualizarSaldo();
        actualizarListaEmpresas();
        actualizarDatosTabla();
    }

    public void comprar(){
        // Si alguno de los campos necesarios está vacío, no se hace nada
        if (campoPrecio.getText().isEmpty() || campoNumero.getText().isEmpty() || empresaComboBox.getSelectionModel().getSelectedIndex() == -1)
            return;

        // Variables de estado
        float saldo;
        Integer acomprar = Integer.parseInt(campoNumero.getText());
        Integer compradas = 0;
        Venta ventaHecha;
        Integer partPosibles;

        // INICIAMOS TRANSACCION
        super.iniciarTransaccion();
        //Recojemos los datos actualizados
        actualizarDatosTabla();
        actualizarSaldo();
        saldo = Float.parseFloat(campoSaldo.getText());

        // Compramos de las ofertas de más baratas a más caras
        for (OfertaVenta oferta : datosTabla) {
            // Si se compraron las solicitadas o no hay dinero para más se para el bucle
            if (acomprar - compradas == 0) break; //
            if ((partPosibles = (int) Math.floor(saldo / oferta.getPrecioVenta())) == 0) break;

            partPosibles = Math.min(partPosibles, acomprar - compradas);
            partPosibles = Math.min(partPosibles, oferta.getNumParticipaciones());

            oferta.setNumParticipaciones(oferta.getNumParticipaciones() - partPosibles);
            compradas += partPosibles;
            saldo -= partPosibles * oferta.getPrecioVenta();

            ventaHecha = new Venta.Builder().withCantidad(partPosibles)
                    .withOfertaVenta(oferta)
                    .withFecha(new Date(System.currentTimeMillis()))
                    .withUsuarioCompra(usr.getSuperUsuario())
                    .build();
            getDAO(VentaDAO.class).insertar(ventaHecha);
            getDAO(OfertaVentaDAO.class).actualizar(oferta);
        }
        // Actualizamos saldo y elementos gráficos
        usr.setSaldo(saldo+usr.getSaldoBloqueado());
        getDAO(UsuarioDAO.class).actualizar(usr);
        actualizarDatosTabla();
        actualizarSaldo();

        // Tratamos de comprometer la transacción
        if (!super.ejecutarTransaccion()) {
            notificationBar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Compra realizada con éxito!"), Duration.seconds(3.0), null));
        }else{
            notificationBar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Compra fallida!"), Duration.seconds(3.0), null));
        }
    }

    // BOTONES //

    // Boton de salir
    public void btnSalirEvent(ActionEvent event) {
        ((Stage) btnSalir.getScene().getWindow()).close();
    }

    // Nueva empresa seleccionada
    public void btnEmpresaEvent(ActionEvent event) {
        actualizarDatosTabla();
    }

    // Boton de comprar
    public void btnComprarEvent(ActionEvent event) {
        comprar();
    }

    // Boton de refresco
    public void btnRefreshEvent(ActionEvent event){
        actualizarVentana();
    }


}
