package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.util.DatabaseLinker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SampleController extends DatabaseLinker {
    @FXML
    private JFXButton prueba;

    public void prueba(ActionEvent actionEvent) {
        Usuario usuario = super.getDAO(UsuarioDAO.class).get("diego");
        System.out.println(usuario.getDireccion());
    }
}

