package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.DoubleValidator;
import com.jfoenix.validation.IntegerValidator;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.CompraDAO;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import com.jfoenix.validation.RequiredFieldValidator;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class VCompraController extends DatabaseLinker {
    @FXML
    private JFXButton btnSalir;
    @FXML
    private JFXTextField numeroPar;
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

    private List<Empresa> listaEmpresas;
    private ObservableList<OfertaVenta> datosTabla;

    @FXML
    public void initialize() {

        listaEmpresas = new ArrayList<>();
        datosTabla = FXCollections.observableArrayList();

        // Setup de las columnas de la tabla
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precio_venta"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cantidadCol.setCellValueFactory(new PropertyValueFactory<>("num_participaciones"));
        tablaOfertas.setItems(datosTabla);

        // Llenamos la lista de empresas y el ComboBox
        for (Empresa e : getDAO(EmpresaDAO.class).getEmpresas()) {
            listaEmpresas.add(e);
            empresaComboBox.getItems().add(e.getNombre() + " || " + e.getCif());
        }

        //Añadimos el listener al campo precio
        campoPrecio.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!campoPrecio.getText().isEmpty() && tablaOfertas.getSelectionModel().getSelectedIndex() != -1)
                    actualizarDatosTabla();
            }
        });
    }

    public void controladorNumeros(ActionEvent event) {
        //controlador de integers en el campo de número de participaciones
        IntegerValidator iv = new IntegerValidator("Introduce un número de participaciones válido");
        numeroPar.getValidators().add(iv);
        if (!numeroPar.validate()) {
            this.numeroPar.setText("");
        }

    }


    public void btnComprarEvent(ActionEvent event) {
        //realiza la compra de las participaciones de menor a mayor precio hasta que el usuario no tenga más saldo
        IntegerValidator iv = new IntegerValidator("");
        OfertaVenta ofertaMenor;
        //TODO pasar usuario correspondiente
        Usuario usuario = super.getDAO(UsuarioDAO.class).getUsuario("nere");
        numeroPar.getValidators().add(iv);
        double totalprecio = 0;
        int numero = 0;
        DoubleValidator dv = new DoubleValidator("Numero no válido");
        this.campoPrecio.getValidators().add(dv);
        if (!this.numeroPar.getText().equals("") && numeroPar.validate() && campoPrecio.validate()) {

        } else if (usuario.getSaldo() < ((totalprecio = Integer.parseInt(this.numeroPar.getText()) * Double.parseDouble(this.campoPrecio.getText())))) {

        } else {
            Empresa empresa = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex());
            ArrayList<OfertaVenta> ofertas = new ArrayList<OfertaVenta>();
            for (OfertaVenta ov : datosTabla) {
                if (ov.getEmpresa().getCif().equals(empresa.getCif())
                        && ov.getPrecioVenta() <= Double.parseDouble(this.campoPrecio.getText())) {
                    ofertas.add(ov);
                }
            }



            while (!ofertas.isEmpty() && totalprecio >= 0) {
                ofertaMenor = ofertas.get(seleccionar_MenorPrecio(ofertas));
                if (ofertaMenor.getNumParticipaciones() <=  Integer.parseInt(this.numeroPar.getText())){
                        getDAO(OfertaVentaDAO.class).cerrarOfertaVenta(ofertaMenor);
                }
                else{

                }


            }

        }


    }

    public int seleccionar_MenorPrecio(List<OfertaVenta> ofertas) {
        int posicion = 0;
        int posicionMenor = 0;
        float precio = ofertas.get(0).getPrecioVenta();
        for (OfertaVenta e : ofertas) {

            if (precio > e.getPrecioVenta()) {
                posicionMenor = posicion;
            }
            posicion++;
        }

        return posicionMenor;

    }


    public void empresaSelected(ActionEvent event) {
        if (!campoPrecio.getText().isEmpty()) actualizarDatosTabla();
    }

    public void actualizarDatosTabla() {
        List<OfertaVenta> ofertas;
        Empresa empresa = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex());
        ofertas = getDAO(CompraDAO.class).getOfertasVenta(empresa.getCif(), Float.parseFloat(campoPrecio.getText()));
        datosTabla.setAll(ofertas);
    }

    public void btnSalirEvent(ActionEvent event) {
        ((Stage) btnSalir.getScene().getWindow()).close();
    }


}
