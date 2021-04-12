package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class VCompraController extends DatabaseLinker {
    @FXML
    private JFXButton btnSalir;


    public void btnSalirEvent(ActionEvent event) {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    public void buscarPorNombre(ActionEvent event){

    }

    public void buscarPorPrecio(ActionEvent event){

    }


}
