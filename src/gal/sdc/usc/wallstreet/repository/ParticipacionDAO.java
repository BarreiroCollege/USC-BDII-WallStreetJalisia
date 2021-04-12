package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

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

    /**
     * Devuelve todas las participaciones de un usuario indicado.
     *
     * @param u Usuario que posee las participaciones
     * @return List<Participacion> con las participaciones correspondientes
     */
    public List<Participacion> getParticipaciones(Usuario u){
        return getParticipaciones(u.getIdentificador());
    }

    /**
     * Devuelve todas las participaciones a nombre de un usuario.
     *
     * @param idUsuario usuario que posee las participaciones
     * @return List<Participacion> con las participaciones correspondientes
     */
    public List<Participacion> getParticipaciones(String idUsuario) {
        List<Participacion> participaciones = new ArrayList<>();
        Usuario usuario = new Usuario.Builder(idUsuario).build();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT empresa, cantidad, cantidad_bloqueada " +
                        "FROM participacion WHERE usuario = ?"
        )) {
            ps.setString(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Participacion participacion = new Participacion.Builder().withUsuario(usuario)
                        .withEmpresa(
                                new Empresa.Builder(
                                        new Usuario.Builder(rs.getString("empresa")).build()
                                ).build()
                        )
                        .withCantidad(rs.getInt("cantidad"))
                        .withCantidadBloqueada(rs.getInt("cantidad_bloqueada")).build();
                participaciones.add(participacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participaciones;
    }
}
