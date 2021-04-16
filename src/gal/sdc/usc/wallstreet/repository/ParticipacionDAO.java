package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipacionDAO extends DAO<Participacion> {
    public ParticipacionDAO(Connection conexion) {
        super(conexion, Participacion.class);
    }

    public List<Participacion> getParticipacionesPorUsuario(String nombreUsuario) {
        List<Participacion> participaciones = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM participacion where usuario = ?"
        )) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                participaciones.add(Mapeador.map(rs, Participacion.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participaciones;
    }
}
