package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.*;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
