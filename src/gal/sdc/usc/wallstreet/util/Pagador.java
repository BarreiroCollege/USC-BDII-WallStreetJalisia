package gal.sdc.usc.wallstreet.util;

import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.repository.PagoDAO;
import gal.sdc.usc.wallstreet.repository.PagoUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.SociedadDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;

import java.util.List;

public class Pagador extends DatabaseLinker {
    private Pagador() {
    }

    public static void despacharPagosProgramados() {
        Pagador pagador = new Pagador();
        pagador.pagar();
    }

    private void pagar() {
        super.iniciarTransaccion();

        List<Pago> pagos = super.getDAO(PagoDAO.class).getPagosProgramadosPendientesDeEjecutar();

        for (Pago pago : pagos) {
            List<PagoUsuario> pagoUsuarios = super.getDAO(PagoUsuarioDAO.class).getPagoUsarios(pago);

            float saldoAQuitar = 0.0f;
            int participacionesAQuitar = 0;
            for (PagoUsuario pu : pagoUsuarios) {
                saldoAQuitar = pu.getBeneficioRecibir();
                participacionesAQuitar += pu.getParticipacionesRecibir();
            }

            super.getDAO(PagoDAO.class).quitarSaldoBloqueado(pago, saldoAQuitar);
            super.getDAO(PagoDAO.class).quitarParticipacionesBloqueadas(pago, participacionesAQuitar);

            for (PagoUsuario pu : pagoUsuarios) {
                super.getDAO(PagoUsuarioDAO.class).recibirPago(
                        pu,
                        super.getDAO(UsuarioDAO.class),
                        super.getDAO(SociedadDAO.class)
                );
            }

            pago.setFechaAnuncio(null);
            super.getDAO(PagoDAO.class).actualizar(pago);
        }

        super.ejecutarTransaccion();
    }
}
