package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class PartEmpresaController extends DatabaseLinker {
    public static final String VIEW = "partEmpresa";
    public static final Integer HEIGHT = 431;
    public static final Integer WIDTH = 439;
    public static final String TITULO = "Gestión de participaciones";

    @FXML
    private JFXButton btnCerrarVentana;

    @FXML
    public void initialize(){

    }

    public void cerrarVentana(){
        ((Stage) btnCerrarVentana.getScene().getWindow()).close();
    }

}
