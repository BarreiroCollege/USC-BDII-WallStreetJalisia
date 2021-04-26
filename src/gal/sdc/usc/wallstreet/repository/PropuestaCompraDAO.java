package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.PropuestaCompra;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PropuestaCompraDAO extends DAO<PropuestaCompra> {
    public PropuestaCompraDAO(Connection conexion) {
        super(conexion, PropuestaCompra.class);
    }

    public List<PropuestaCompra> getPropuestasPorSociedad(Sociedad s) {
        List<PropuestaCompra> pcs = new LinkedList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM propuesta_compra WHERE sociedad=?"
        )) {
            ps.setString(1, s.getIdentificador().getIdentificador());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pcs.add(Mapeador.map(rs, PropuestaCompra.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return pcs;
    }
}
