package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.CompraDAO;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
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
import java.util.Date;
import java.util.List;

public class VCompraController extends DatabaseLinker {
    @FXML private JFXButton btnSalir;
    @FXML private JFXComboBox<String> empresaComboBox;
    @FXML private JFXTextField campoPrecio;
    @FXML private TableView<OfertaVenta> tablaOfertas;
    @FXML private TableColumn<OfertaVenta, String> nombreCol;
    @FXML private TableColumn<OfertaVenta, Float> precioCol;
    @FXML private TableColumn<OfertaVenta, Date> fechaCol;
    @FXML private TableColumn<OfertaVenta, Integer> cantidadCol;

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
        for(Empresa e : getDAO(EmpresaDAO.class).getEmpresas()){
            listaEmpresas.add(e);
            empresaComboBox.getItems().add(e.getNombre()+" || "+e.getCif());
        }

        //Añadimos el listener al campo precio
        campoPrecio.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!campoPrecio.getText().isEmpty() && tablaOfertas.getSelectionModel().getSelectedIndex()!=-1) actualizarDatosTabla();
            }
        });
    }

    public void btnComprarEvent(ActionEvent event) {

    }

    public void empresaSelected(ActionEvent event){
        if(!campoPrecio.getText().isEmpty()) actualizarDatosTabla();
    }

    public void actualizarDatosTabla(){
        List<OfertaVenta> ofertas;
        Empresa empresa = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex());
        ofertas = getDAO(CompraDAO.class).getOfertasVenta(empresa.getCif(), Float.parseFloat(campoPrecio.getText()));
        datosTabla.setAll(ofertas);
    }

    public void btnSalirEvent(ActionEvent event) {
        ((Stage) btnSalir.getScene().getWindow()).close();
    }

}
