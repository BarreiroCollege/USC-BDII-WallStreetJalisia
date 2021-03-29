package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.repository.PagoUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.List;

public class SampleController extends DatabaseLinker {
    @FXML
    private JFXButton prueba;

    public void prueba(ActionEvent actionEvent) {
        List<PagoUsuario> pagos = super
                .getDAO(PagoUsuarioDAO.class)
                .getPagosHastaAhora();
        for (PagoUsuario pago : pagos) {
            System.out.println(pago);
        }
    }
}
