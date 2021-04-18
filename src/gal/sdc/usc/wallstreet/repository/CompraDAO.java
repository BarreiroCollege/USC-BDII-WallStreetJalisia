package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Compra;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO extends DAO<Compra> {
    public CompraDAO(Connection conexion) {
        super(conexion, Compra.class);
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
}
