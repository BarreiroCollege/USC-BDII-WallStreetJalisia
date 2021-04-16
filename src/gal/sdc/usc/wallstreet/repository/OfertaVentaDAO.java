package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfertaVentaDAO extends DAO<OfertaVenta> {
    public OfertaVentaDAO(Connection conexion) {
        super(conexion, OfertaVenta.class);
    }

    /**
     * Devuelve todas las ofertas de venta de un usuario indicado.
     *
     * @param u Usuario que posee las ofertas
     * @return List<OfertaVenta> con las ofertas correspondientes
     */
    public List<OfertaVenta> getOfertasVenta(Usuario u) {
        return getOfertasVenta(u.getIdentificador());
    }

    /**
     * Devuelve todas las ofertas de venta a nombre de un usuario.
     *
     * @param idUsuario usuario que posee las ofertas
     * @return List<OfertaVenta> con las ofertas correspondientes
     */
    public List<OfertaVenta> getOfertasVenta(String idUsuario) {
        List<OfertaVenta> ofertas = new ArrayList<>();
        Usuario usuario = new Usuario.Builder(idUsuario).build();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT o.empresa, e.nombre, e.cif, o.num_participaciones, o.precio_venta " +
                        "FROM oferta_venta o JOIN empresa e ON o.empresa = e.usuario " +
                        "WHERE o.usuario = ?"
        )) {
            ps.setString(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                OfertaVenta ofertaventa = new OfertaVenta.Builder().withUsuario(usuario)
                        .withEmpresa(
                                new Empresa.Builder(
                                        new Usuario.Builder(rs.getString("empresa")).build()
                                )
                                        .withCif(rs.getString("cif"))
                                        .withNombre(rs.getString("nombre")).build()
                        )
                        .withNumParticipaciones(rs.getInt("num_participaciones"))
                        .withPrecioVenta(rs.getFloat("precio_venta")).build();

                try (PreparedStatement psFecha = conexion.prepareStatement(
                        "SELECT max(fecha) FROM oferta_venta WHERE empresa = ?"
                )) {
                    psFecha.setString(1, ofertaventa.getEmpresa().getUsuario().getIdentificador() );
                    ResultSet rsFecha = psFecha.executeQuery();
                    if (rsFecha.next()) {
                        ofertaventa.getEmpresa().setFechaUltimoPago(rsFecha.getTimestamp(1));
                    }
                }

                ofertas.add(ofertaventa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ofertas;
    }
}
