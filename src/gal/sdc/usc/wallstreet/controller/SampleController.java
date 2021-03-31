package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.repository.PagoDAO;
import gal.sdc.usc.wallstreet.repository.PagoUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.Date;
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

        Pago pago = new Pago.Builder(new Date(), pagos.get(0).getPago().getEmpresa())
                .withBeneficioPorParticipacion(3.1f)
                .build();
        PagoUsuario pagoUsuario = new PagoUsuario.Builder(pagos.get(0).getUsuario(), pago)
                .withNumParticipaciones(30)
                .build();
        super.getDAO(PagoUsuarioDAO.class).crear(pagoUsuario);
        super.getDAO(PagoUsuarioDAO.class).borrar(pagoUsuario);
        super.getDAO(PagoDAO.class).borrar(pago);
    }
}
