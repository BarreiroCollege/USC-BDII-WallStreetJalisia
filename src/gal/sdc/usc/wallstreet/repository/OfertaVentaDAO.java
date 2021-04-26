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

    public List<OfertaVenta> getOfertasVenta(String empresa, Float precioMax) {
        List<OfertaVenta> ofertas = new ArrayList<>();
        try{
            String statement = "SELECT * FROM oferta_venta " +
                                "WHERE confirmado is true and empresa=? ";
            if(precioMax.equals(0f)) statement += "and precio_venta<=? ";
            statement += "ORDER BY precio_venta asc";

            PreparedStatement ps = conexion.prepareStatement(statement);
            ps.setString(1,empresa);
            if(precioMax.equals(0f)) ps.setFloat(2,precioMax);
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


}


