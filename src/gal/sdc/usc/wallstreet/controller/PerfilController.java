package gal.sdc.usc.wallstreet.controller;

import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PerfilController extends DatabaseLinker implements Initializable  {
    public static final String VIEW = "perfil";
    public static final Integer HEIGHT = 600;
    public static final Integer WIDTH = 800;
    public static final String TITULO = "Mi Perfil";

    private BooleanProperty editando = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
