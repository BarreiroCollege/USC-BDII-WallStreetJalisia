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

import java.util.Date;
import java.util.List;

public class SampleController extends DatabaseLinker {
    @FXML
    private JFXButton prueba;

    public void prueba(ActionEvent actionEvent) {
        /* List<PagoUsuario> pagos = super
                .getDAO(PagoUsuarioDAO.class)
                .getPagosHastaAhora();
        for (PagoUsuario pago : pagos) {
            System.out.println(pago);
        } */

        /* Pago pago = new Pago.Builder(new Date(), pagos.get(0).getPago().getEmpresa())
                .withBeneficioPorParticipacion(3.1f)
                .build();
        PagoUsuario pagoUsuario = new PagoUsuario.Builder(pagos.get(0).getUsuario(), pago)
                .withNumParticipaciones(30)
                .build();

        super.getDAO(PagoUsuarioDAO.class).crear(pagoUsuario);
        super.getDAO(PagoUsuarioDAO.class).borrar(pagoUsuario);
        super.getDAO(PagoDAO.class).borrar(pago); */

        /* Empresa empresa = new Empresa.Builder()
                .withUsuario(
                        new Usuario.Builder()
                        .withIdentificador("mudi")
                        .withClave("Mudi")
                        .withSaldo(0f)
                        .build()
                )
                .withCif("Pepito")
                .withNombre("Palotes")
                .build();

        super.getDAO(EmpresaDAO.class).crear(empresa); */

        /* Usuario diego = super.getDAO(UsuarioDAO.class).seleccionar("diego");
        Empresa diegoEmpresa = super.getDAO(EmpresaDAO.class).seleccionar(diego);

        Participacion participacion = new Participacion.Builder()
                .withEmpresa(diegoEmpresa)
                .withUsuario(diego)
                .withCantidad(65)
                .build();

        super.getDAO(ParticipacionDAO.class).crear(participacion); */

        /* for (Usuario usuario : super.getDAO(UsuarioDAO.class).getUsuarios()) {
            System.out.println(usuario);
        } */
    }
}
