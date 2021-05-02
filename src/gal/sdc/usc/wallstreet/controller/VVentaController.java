package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Regulador;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioComprador;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.ReguladorDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Iconos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.IntegerStringConverter;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class VVentaController extends DatabaseLinker {

    public static final String VIEW = "vventa";
    public static final Integer HEIGHT = 500;
    public static final Integer WIDTH = 800;
    public static final String TITULO = "Vender participaciones";

    private UsuarioComprador usr;
    private ObservableList<OfertaVenta> datosTabla;
    private List<Participacion> listaEmpresas;
    @FXML
    private JFXButton btnVolver;
    @FXML
    private TableView<OfertaVenta> tablaOfertas;
    @FXML
    private TableColumn<OfertaVenta, String> empresaCol;
    @FXML
    private TableColumn<OfertaVenta, Integer> restantesCol;
    @FXML
    private TableColumn<OfertaVenta, Float> precioCol;
    @FXML
    private TableColumn<OfertaVenta, Boolean> confirmadoCol;
    @FXML
    private TableColumn<OfertaVenta, Date> fechaCol;

    @FXML
    private JFXTextField campoNumero;
    @FXML
    private JFXTextField campoPrecio;
    @FXML
    private JFXTextField campoParticipaciones;
    @FXML
    private JFXButton botonRefresh;
    @FXML
    private JFXComboBox<String> empresaComboBox;
    @FXML
    private JFXTextField campoComision;

    @FXML
    private JFXButton btnLanzar;
    @FXML
    private JFXButton btnRetirar;
    @FXML
    private JFXToggleButton tglSociedad;

    @FXML
    public void initialize() {

        datosTabla = FXCollections.observableArrayList();

        // Recuperamos el usuario
        usr = super.getUsuarioSesion().getUsuario();

        // Setup de las columnas de la tabla
        tablaOfertas.setOnSort(Event::consume); // Impedimos reordenamiento
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        restantesCol.setCellValueFactory(new PropertyValueFactory<>("restantes"));
        confirmadoCol.setCellValueFactory(new PropertyValueFactory<>("confirmado"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        tablaOfertas.setItems(datosTabla);

        tablaOfertas.setPlaceholder(new Label("")); // Eliminamos el texto por defecto (estético)
        empresaCol.setCellValueFactory(p -> {
            if (p.getValue() != null) {
                return new SimpleStringProperty(p.getValue().getEmpresa().getNombre());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });

        // Formateo de los campos numericos
        campoNumero.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) campoNumero.setText(oldValue);
        });
        campoPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*|\\d+\\.\\d{0,2}")) campoPrecio.setText(oldValue);
        });

        // Cargamos saldo y preparamos botones de refresh
        botonRefresh.setGraphic(Iconos.icono(FontAwesomeIcon.REFRESH, "1em"));

        btnVolver.setOnAction(e -> Main.ventana(
                PrincipalController.VIEW,
                PrincipalController.WIDTH,
                PrincipalController.HEIGHT,
                PrincipalController.TITULO
        ));

        if (super.getUsuarioSesion().getUsuario().getSociedad() == null) {
            tglSociedad.setVisible(false);
        }

        tglSociedad.setOnAction(e -> {
            if (!super.getUsuarioSesion().getUsuario().getLider()) {
                btnRetirar.setDisable(tglSociedad.isSelected());
                btnLanzar.setDisable(tglSociedad.isSelected());
            }

            if (tglSociedad.isSelected()) {
                usr = super.getUsuarioSesion().getUsuario().getSociedad();
            } else {
                usr = super.getUsuarioSesion().getUsuario();
            }

            actualizarVentana();
        });

        actualizarVentana();
    }

    public void actualizarListaEmpresas() {
        // Se carga la nueva lista
        listaEmpresas = getDAO(ParticipacionDAO.class).getParticipaciones(usr.getSuperUsuario().getIdentificador());
        // Se limpia la comboBox y se vuelve a llenar
        empresaComboBox.getItems().clear();
        for (Participacion e : listaEmpresas) {
            empresaComboBox.getItems().add(e.getEmpresa().getNombre() + " || " + e.getEmpresa().getCif());
        }
    }

    public void actualizarDatosTabla() {
        // Si no hay ninguna empresa seleccionada, se limpia la tabla
        if (empresaComboBox.getSelectionModel().getSelectedIndex() == -1) {
            datosTabla.clear();
            return;
        }
        datosTabla.setAll(getDAO(OfertaVentaDAO.class).getOfertasVentaUsuario(
                listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getEmpresa().getUsuario().getSuperUsuario().getIdentificador(),
                usr.getSuperUsuario().getIdentificador())
        );

        actualizarParticipaciones();

    }

    // Actualiza el campo del saldo de participaciones que el usuario tiene de la empresa seleccionada
    public void actualizarParticipaciones() {
        // Si no hay ninguna empresa seleccionada, se pone a 0
        if (empresaComboBox.getSelectionModel().getSelectedIndex() == -1) {
            campoParticipaciones.setText("0");
            return;
        }
        campoParticipaciones.setText(String.valueOf(getDAO(ParticipacionDAO.class).getParticipacionesUsuarioEmpresa(usr.getSuperUsuario().getIdentificador(),
                listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getEmpresa().getUsuario().getSuperUsuario().getIdentificador())));
    }


    public void nuevaOfertaVenta() {
        // Si alguno de los campos necesarios está vacio, se para
        if (campoPrecio.getText().isEmpty() || campoNumero.getText().isEmpty() || empresaComboBox.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }

        // INICIAMOS TRANSACCION
        super.iniciarTransaccion();
        Boolean ejecutada = true;
        // Si no tiene suficientes o el precio es 0, se informa al usuario y se para
        if (Integer.parseInt(campoNumero.getText()) > Integer.parseInt(campoParticipaciones.getText()) || Integer.parseInt(campoNumero.getText()) <= 0) {
            Main.mensaje("No dispone de suficientes", 3);
            ejecutada = false;
        } else if (Float.parseFloat(campoPrecio.getText()) <= 0) {
            Main.mensaje("Introduzca un precio válido", 3);
            ejecutada = false;
        } else {
            Empresa empresa = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getEmpresa();
            Regulador r = super.getDAO(ReguladorDAO.class).getRegulador();
            OfertaVenta oferta = new OfertaVenta.Builder().withPrecioVenta(Float.parseFloat(campoPrecio.getText())).
                    withEmpresa(empresa).
                    withUsuario(usr.getSuperUsuario()).
                    withConfirmado(false).
                    withNumParticipaciones(Integer.parseInt(campoNumero.getText())).
                    withComision(usr instanceof Usuario ? r.getComision() : r.getComisionSociedad())
                    .build();
            // Insertamos la oferta
            getDAO(OfertaVentaDAO.class).insertar(oferta);
            // Incrementamos su saldo de participaciones bloqueadas
            Participacion cartera = super.getDAO(ParticipacionDAO.class).seleccionar(usr.getSuperUsuario(), empresa);
            if(cartera == null){
                cartera = new Participacion.Builder()
                        .withEmpresa(empresa)
                        .withUsuario(usr.getSuperUsuario())
                        .build();
                cartera.setCantidadBloqueada(cartera.getCantidadBloqueada() + oferta.getNumParticipaciones());
                super.getDAO(ParticipacionDAO.class).insertar(cartera);
            } else {
                cartera.setCantidadBloqueada(cartera.getCantidadBloqueada() + oferta.getNumParticipaciones());
                super.getDAO(ParticipacionDAO.class).actualizar(cartera);
            }
        }
        // Tratamos de comprometer la transacción e informamos al usuario
        String mensaje;
        if (super.ejecutarTransaccion()) {
            mensaje = "Éxito, oferta lanzada!";
        } else {
            mensaje = "Lanzamiento fallido";
        }
        if (ejecutada)
            Main.mensaje(mensaje, 3);

        actualizarVentana();
    }


    public void actualizarVentana() {
        actualizarListaEmpresas();
        actualizarDatosTabla();
        actualizarParticipaciones();
        actualizarComision();
        campoNumero.setText("");
        campoPrecio.setText("");
    }

    public void actualizarComision() {
        Regulador r = super.getDAO(ReguladorDAO.class).getRegulador();
        float comision = usr instanceof Usuario ? r.getComision() : r.getComisionSociedad();
        campoComision.setText(new DecimalFormat("0.00").format(comision * 100) + " %");
    }


    public void retirarOferta() {
        // SI no hay nada seleccionado se para
        if (tablaOfertas.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }
        OfertaVenta oferta = tablaOfertas.getSelectionModel().getSelectedItem();

        Empresa empresa = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getEmpresa();

        // INICIAMOS TRANSACCION
        super.iniciarTransaccion();

        // Se ponen las participaciones de la oferta a 0 y se le desbloquean de su saldo
        Participacion saldo = super.getDAO(ParticipacionDAO.class).seleccionar(usr.getSuperUsuario(), empresa);
        saldo.setCantidadBloqueada(saldo.getCantidadBloqueada() - oferta.getRestantes());
        super.getDAO(ParticipacionDAO.class).actualizar(saldo);
        oferta.setRestantes(0);
        super.getDAO(OfertaVentaDAO.class).actualizar(oferta);

        // Tratamos de comprometer la transacción e informamos al usuario
        String mensaje;
        if (super.ejecutarTransaccion()) {
            mensaje = "Éxito, oferta retirada!";
        } else {
            mensaje = "Error, no se pudo retirar";
        }
        Main.mensaje(mensaje, 3);

        actualizarVentana();
    }

}
