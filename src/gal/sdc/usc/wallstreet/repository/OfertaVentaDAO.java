package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.*;

public class OfertaVentaDAO extends DAO<OfertaVenta> {


    public OfertaVentaDAO(Connection conexion) {
        super(conexion, OfertaVenta.class);
    }

    public void cerrarOfertaVenta(OfertaVenta ofertaVenta) {
        PreparedStatement stmUsuario = null;


        try (PreparedStatement ps = conexion.prepareStatement(
                "update oferta_venta " +
                        "set confirmado=? " +
                        "where oferta_venta.empresa=? and oferta_venta.fecha_anuncio= ?"
        )) {
            ps.setBoolean(1, true);
            ps.setString(2, ofertaVenta.getEmpresa().getCif());
            ps.setDate(3, (Date) ofertaVenta.getFecha());
            stmUsuario.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}