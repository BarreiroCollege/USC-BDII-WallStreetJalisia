package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.util.DatabaseLinker;
import javafx.fxml.FXML;

public class SampleController extends DatabaseLinker {
    @FXML
    private JFXButton prueba;

    private void prueba() {
        UsuarioDAO dao = (UsuarioDAO) super.getDAO(UsuarioDAO.class);
        dao.getUsuarioPorIdentificador("fkgjhbndfhj");
    }
}
