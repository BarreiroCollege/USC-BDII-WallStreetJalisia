package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.*;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.*;
import java.util.ArrayList;
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
    public boolean insertarPago(Pago p){
        try (PreparedStatement ps = conexion.prepareStatement(
            "INSERT INTO pago (fecha, empresa, beneficio_por_participacion, participacion_por_participacion, fecha_anuncio, porcentaje_beneficio, porcentaje_participacion)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)"
        )){
            ps.setTimestamp(1, new Timestamp((p.getFecha()).getTime()));
            ps.setString(2, p.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
            ps.setFloat(3, p.getBeneficioPorParticipacion());
            ps.setFloat(4, p.getParticipacionPorParticipacion());
            if(p.getFechaAnuncio() != null) {
                ps.setTimestamp(5, new Timestamp((p.getFechaAnuncio()).getTime()));
            } else{
                ps.setTimestamp(5, null);
            }
            ps.setFloat(6, p.getPorcentajeBeneficio());
            ps.setFloat(7, p.getPorcentajeParticipacion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean actualizarSaldos(Pago p, float dinero, List<Participacion> participacions){
        if((p.getEmpresa().getUsuario().getSaldo() - p.getEmpresa().getUsuario().getSaldoBloqueado()) - (dinero * participacions.size()) < 0){
            Main.mensaje("No se dispone del saldo suficiente", 3);
            return false;
        }
        if(p.getFechaAnuncio() == null) {
            try (PreparedStatement ps = conexion.prepareStatement(
                    "UPDATE usuario SET saldo = ? WHERE usuario.identificador = ?"
            )) {
                ps.setFloat(1, p.getEmpresa().getUsuario().getSaldo() - dinero * participacions.size());
                ps.setString(2, p.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
                ps.executeUpdate();
                for(Participacion participacion : participacions) {
                    System.out.println(participacion.getUsuario().getSuperUsuario().getIdentificador());
                    try (PreparedStatement ps2 = conexion.prepareStatement(
                            "UPDATE usuario SET saldo = ? WHERE usuario.identificador = ?"
                    )) {
                        ps2.setFloat(1, participacion.getUsuario().getSaldo() + dinero * p.getPorcentajeBeneficio() * participacion.getCantidad());
                        ps2.setString(2, participacion.getUsuario().getSuperUsuario().getIdentificador());
                        ps2.executeUpdate();
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
                return true;
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            return false;
        } else{
            try (PreparedStatement ps = conexion.prepareStatement(
                    "UPDATE usuario SET saldo_bloqueado = ? WHERE usuario.identificador = ?"
            )) {
                ps.setFloat(1, p.getEmpresa().getUsuario().getSaldoBloqueado() + dinero * participacions.size());
                ps.setString(2, p.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
                ps.executeUpdate();
                for(Participacion participacion : participacions) {
                    System.out.println(participacion.getUsuario().getSuperUsuario().getIdentificador());
                    try (PreparedStatement ps2 = conexion.prepareStatement(
                            "UPDATE usuario SET saldo = ? WHERE usuario.identificador = ?"
                    )) {
                        ps2.setFloat(1, participacion.getUsuario().getSaldo() + dinero * p.getPorcentajeBeneficio() * participacion.getCantidad());
                        ps2.setString(2, participacion.getUsuario().getSuperUsuario().getIdentificador());
                        ps2.executeUpdate();
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
                return true;
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            return false;
        }
    }

    public boolean repartirParticipaciones(Pago p, List<Participacion> participacions, int cantidad){
        if(p.getFechaAnuncio() == null) {
            for (Participacion pa : participacions) {
                try (PreparedStatement ps2 = conexion.prepareStatement(
                        "UPDATE participacion SET cantidad = ? WHERE participacion.usuario = ? AND participacion.empresa = ?"
                )) {
                    ps2.setFloat(1, pa.getCantidad() + cantidad);
                    ps2.setString(2, pa.getUsuario().getSuperUsuario().getIdentificador());
                    ps2.setString(3, pa.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
                    ps2.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }
            return true;
        } else{
            for (Participacion pa : participacions) {
                try (PreparedStatement ps2 = conexion.prepareStatement(
                        "UPDATE participacion SET cantidad = ? WHERE participacion.usuario = ? AND participacion.empresa = ?"
                )) {
                    ps2.setFloat(1, pa.getCantidad() + cantidad);
                    ps2.setString(2, pa.getUsuario().getSuperUsuario().getIdentificador());
                    ps2.setString(3, pa.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
                    ps2.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }
            return true;
        }
    }
}
