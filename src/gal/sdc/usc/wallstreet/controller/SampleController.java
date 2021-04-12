package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.PagoDAO;
import gal.sdc.usc.wallstreet.repository.PagoUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.swing.JButton;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SampleController extends DatabaseLinker {
    @FXML
    private Button prueba;

    public void prueba(ActionEvent actionEvent) {
        /* List<PagoUsuario> pagos = super
                .getDAO(PagoUsuarioDAO.class)
                .getPagosHastaAhora();
        for (PagoUsuario pago : pagos) {
            System.out.println(pago);
            pago.setNumParticipaciones(new Random().nextInt());
            super.getDAO(PagoUsuarioDAO.class).actualizar(pago);
        }

        Pago pago = new Pago.Builder(new Date(), pagos.get(0).getPago().getEmpresa())
                .withBeneficioPorParticipacion(3.1f)
                .build();
        PagoUsuario pagoUsuario = new PagoUsuario.Builder(pagos.get(0).getUsuario(), pago)
                .withNumParticipaciones(30)
                .build();

        super.getDAO(PagoUsuarioDAO.class).insertar(pagoUsuario);
        super.getDAO(PagoUsuarioDAO.class).eliminar(pagoUsuario);
        super.getDAO(PagoDAO.class).eliminar(pago);

        Empresa empresa = new Empresa.Builder()
                .withUsuario(
                        new Usuario.Builder()
                        .withIdentificador("mudi3")
                        .withClave("Mudi")
                        .withSaldo(0f)
                        .build()
                )
                .withCif("Pepito")
                .withNombre("Palotes")
                .build();

        super.getDAO(EmpresaDAO.class).insertar(empresa);

        Usuario diego = super.getDAO(UsuarioDAO.class).seleccionar("mudi");
        Empresa diegoEmpresa = super.getDAO(EmpresaDAO.class).seleccionar(diego);

        Participacion participacion = new Participacion.Builder()
                .withEmpresa(diegoEmpresa)
                .withUsuario(diego)
                .withCantidad(65)
                .build();

        super.getDAO(ParticipacionDAO.class).insertar(participacion);

        for (Usuario usuario : super.getDAO(UsuarioDAO.class).getUsuarios()) {
            System.out.println(usuario);
        } */
    }
}
