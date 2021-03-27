package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.repository.PagosDAO;
import gal.sdc.usc.wallstreet.util.DatabaseLinker;
import javafx.fxml.FXML;

public class SampleController extends DatabaseLinker {
    @FXML
    private JFXButton prueba;

    private void prueba() {
        PagosDAO dao = (PagosDAO) super.getDAO(PagosDAO.class);
        dao.carga();
    }
}
