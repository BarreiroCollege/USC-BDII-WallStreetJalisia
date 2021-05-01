package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PagoDAO extends DAO<Pago> {
    public PagoDAO(Connection conexion) {
        super(conexion, Pago.class);
    }

    public List<Pago> getPagos(Usuario u) {
        return getPagos(u.getSuperUsuario().getIdentificador());
    }

    public List<Pago> getPagos(String idEmpresa) {
        List<Pago> pagos = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT p.fecha, p.empresa, e.nombre, e.cif, p.beneficio_por_participacion, p.participacion_por_participacion, p.fecha_anuncio, p.porcentaje_beneficio, p.porcentaje_participacion " +
                        "FROM pago p JOIN empresa e ON p.empresa = e.usuario WHERE p.empresa = ?"
        )) {
            ps.setString(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pago pago = new Pago.Builder().withFecha(rs.getTimestamp("fecha"))
                        .withEmpresa(
                                new Empresa.Builder(
                                        new Usuario.Builder(new SuperUsuario.Builder(rs.getString("empresa")).build()).build()
                                )
                                        .withCif(rs.getString("cif"))
                                        .withNombre(rs.getString("nombre")).build()
                        )
                        .withBeneficioPorParticipacion(rs.getFloat("beneficio_por_participacion"))
                        .withParticipacionPorParticipacion(rs.getFloat("participacion_por_participacion"))
                        .withFechaAnuncio(rs.getTimestamp("fecha_anuncio"))
                        .withPorcentajeBeneficio(rs.getFloat("porcentaje_beneficio"))
                        .withPorcentajeParticipacion(rs.getFloat("porcentaje_participacion"))
                        .build();

                pagos.add(pago);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pagos;
    }

    public List<Pago> getPagosProgramadosPendientesDeEjecutar() {
        List<Pago> pagos = new LinkedList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM pago WHERE fecha_anuncio IS NOT NULL AND fecha < NOW()"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pagos.add(Mapeador.map(rs, Pago.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return pagos;
    }

    public void bloquearSaldo(Pago pago, float saldoABloquear) {
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "UPDATE usuario SET saldo_bloqueado = (saldo_bloqueado + ?) WHERE usuario = ?"
        )) {
            ps.setFloat(1, saldoABloquear);
            ps.setString(2, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void bloquearParticipaciones(Pago pago, int participacionesABloquear) {
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "UPDATE participacion SET cantidad_bloqueada = (cantidad_bloqueada + ?) WHERE usuario = ? AND empresa = ?"
        )) {
            ps.setInt(1, participacionesABloquear);
            ps.setString(2, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.setString(3, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void quitarSaldo(Pago pago, float saldoABloquear) {
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "UPDATE usuario SET saldo = (saldo - ?) WHERE usuario = ?"
        )) {
            ps.setFloat(1, saldoABloquear);
            ps.setString(2, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void quitarParticipaciones(Pago pago, int participacionesABloquear) {
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "UPDATE participacion SET cantidad = (cantidad - ?) WHERE usuario = ? AND empresa = ?"
        )) {
            ps.setInt(1, participacionesABloquear);
            ps.setString(2, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.setString(3, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void quitarSaldoBloqueado(Pago pago, float saldoABloquear) {
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "UPDATE usuario SET saldo_bloqueado = (saldo_bloqueado - ?) WHERE usuario = ?"
        )) {
            ps.setFloat(1, saldoABloquear);
            ps.setString(2, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void quitarParticipacionesBloqueadas(Pago pago, int participacionesABloquear) {
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "UPDATE participacion SET cantidad_bloqueada = (cantidad_bloqueada - ?) WHERE usuario = ? AND empresa = ?"
        )) {
            ps.setInt(1, participacionesABloquear);
            ps.setString(2, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.setString(3, pago.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
