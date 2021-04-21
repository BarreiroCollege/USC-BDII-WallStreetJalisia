package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

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

    public List<OfertaVenta> getOfertasVentaPorUsuario(String nombreUsuario) {
        List<OfertaVenta> ofertaVenta = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM oferta_venta where usuario = ?"
        )) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ofertaVenta.add(Mapeador.map(rs, OfertaVenta.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ofertaVenta;
    }
}
