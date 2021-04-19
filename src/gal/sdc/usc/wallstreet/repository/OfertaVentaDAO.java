package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfertaVentaDAO extends DAO<OfertaVenta> {


    public OfertaVentaDAO(Connection conexion) {
        super(conexion, OfertaVenta.class);
    }

    public List<OfertaVenta> getOfertasVenta(String empresa, Float precioMax){
        List<OfertaVenta> ofertas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * FROM oferta_venta " +
                        "WHERE confirmado is true and empresa=? and precio_venta<=?" +
                        "ORDER BY precio_venta asc"
        )) {
            ps.setString(1,empresa);
            ps.setFloat(2,precioMax);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OfertaVenta oferta = Mapeador.map(rs, OfertaVenta.class);
                ofertas.add(oferta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ofertas;
    }

    public void cerrarOfertaVenta(OfertaVenta ofertaVenta) {
        PreparedStatement stmUsuario = null;


        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM oferta_venta where oferta_venta.empresa=? and oferta_venta.fecha=?"
        )) {
            ps.setString(1, ofertaVenta.getEmpresa().getCif());
            ps.setDate(2, (Date) ofertaVenta.getFecha());
            stmUsuario.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void diminuirParticipaciones(OfertaVenta ofertaVenta) {
        PreparedStatement stmUsuario = null;


        try (PreparedStatement ps = conexion.prepareStatement(
                "update oferta_venta " +
                        "set oferta_venta.num_participaciones= ? " +
                        "where oferta_venta.empresa=? and oferta_venta.fecha= ?"
        )) {
            ps.setInt(1, ofertaVenta.getNumParticipaciones());
            ps.setString(2, ofertaVenta.getEmpresa().getCif());
            ps.setDate(3, (Date) ofertaVenta.getFecha());
            stmUsuario.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


