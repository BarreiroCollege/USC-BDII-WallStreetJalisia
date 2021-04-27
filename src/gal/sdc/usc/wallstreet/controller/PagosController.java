package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PagosController extends DatabaseLinker {
    public static final String VIEW = "pagos";
    public static final Integer HEIGHT = 555;
    public static final Integer WIDTH = 867;
    public static final String TITULO = "Ventana pagos participaciones";

    @FXML
    private JFXButton buttonVolver;

    @FXML
    private TableView tablaPagosProgramados;

    @FXML
    public void initialize(){

        buttonVolver.setOnAction(event -> {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        });
    }

    /*public void gestionTablaOfertas(List<OfertaVenta> ofertaVentaList) {
        //ofertaVentaList = new ArrayList<>();

        ObservableList<OfertaVenta> ofertasVenta = FXCollections.observableArrayList(ofertaVentaList);
        tablaPagosProgramados.setItems(ofertasVenta);

        //Declaramos el nombre de las columnas

        colEmpresa2.setCellValueFactory(cellData -> Bindings.createObjectBinding(() -> cellData.getValue().getEmpresa().getNombre()));
        colPrecio.setCellValueFactory(new PropertyValueFactory<OfertaVenta, Float>("precioVenta"));
        colNParticipaciones.setCellValueFactory(new PropertyValueFactory<OfertaVenta, Integer>("numParticipaciones"));

    }*/
}
