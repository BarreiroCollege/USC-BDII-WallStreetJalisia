package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class PagoUsuarioDAO extends DAO<PagoUsuario> {
    public PagoUsuarioDAO(Connection conexion) {
        super(conexion, PagoUsuario.class);
    }

    public List<PagoUsuario> getPagoUsarios(Pago pago) {
        List<PagoUsuario> pagos = new LinkedList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM pago_usuario WHERE pago_fecha = ? AND pago_empresa = ?"
        )) {
            ps.setTimestamp(1, new Timestamp(pago.getFecha().getTime()));
            ps.setString(2, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pagos.add(Mapeador.map(rs, PagoUsuario.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return pagos;
    }

    public void recibirPago(PagoUsuario pu, UsuarioDAO usuarioDAO, SociedadDAO sociedadDAO) {
        Usuario u = usuarioDAO.seleccionar(pu.getUsuario());
        Sociedad s = sociedadDAO.seleccionar(pu.getUsuario());

        // Detectar si el poseedor es sociedad o usuario
        if (u != null) {
            try (PreparedStatement ps = super.conexion.prepareStatement(
                    "UPDATE usuario SET saldo = (saldo + ?) WHERE identificador = ?"
            )) {
                ps.setFloat(1, pu.getNumParticipaciones()
                        * pu.getPago().getPorcentajeBeneficio()
                        * pu.getBeneficioRecibir());
                ps.setString(2, u.getSuperUsuario().getIdentificador());
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        } else if (s != null) {
            try (PreparedStatement ps = super.conexion.prepareStatement(
                    "UPDATE sociedad SET saldo_comunal = (saldo_comunal + ?) WHERE identificador = ?"
            )) {
                ps.setFloat(1, pu.getNumParticipaciones()
                        * pu.getPago().getPorcentajeBeneficio()
                        * pu.getBeneficioRecibir());
                ps.setString(2, s.getSuperUsuario().getIdentificador());
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "UPDATE participacion SET cantidad = (cantidad + ?) WHERE usuario = ? AND empresa = ?"
        )) {
            ps.setInt(1, (int) (pu.getNumParticipaciones()
                                * pu.getPago().getPorcentajeParticipacion()
                                * pu.getParticipacionesRecibir()));
            ps.setString(2, pu.getUsuario().getIdentificador());
            ps.setString(3, pu.getPago().getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
