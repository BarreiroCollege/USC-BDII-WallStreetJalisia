package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
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

    /**
     * Devuelve todas las participaciones de un usuario indicado.
     *
     * @param u Usuario que posee las participaciones
     * @return List<Participacion> con las participaciones correspondientes
     */
    public List<Participacion> getParticipaciones(Usuario u) {
        return getParticipaciones(u.getSuperUsuario().getIdentificador());
    }

    /**
     * Devuelve todas las participaciones a nombre de un usuario.
     *
     * @param idUsuario usuario que posee las participaciones
     * @return List<Participacion> con las participaciones correspondientes
     */
    public List<Participacion> getParticipaciones(String idUsuario) {
        List<Participacion> participaciones = new ArrayList<>();
        Usuario usuario = new Usuario.Builder(new SuperUsuario.Builder(idUsuario).build()).build();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT p.empresa, e.nombre, e.cif, p.cantidad, p.cantidad_bloqueada " +
                        "FROM participacion p JOIN empresa e ON p.empresa = e.usuario " +
                        "WHERE p.usuario = ?"
        )) {
            ps.setString(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //TODO: mapeador?
                Participacion participacion = new Participacion.Builder().withUsuario(usuario)
                        .withEmpresa(
                                new Empresa.Builder(
                                        new Usuario.Builder(new SuperUsuario.Builder(rs.getString("empresa")).build()).build()
                                )
                                        .withCif(rs.getString("cif"))
                                        .withNombre(rs.getString("nombre")).build()
                        )
                        .withCantidad(rs.getInt("cantidad"))
                        .withCantidadBloqueada(rs.getInt("cantidad_bloqueada")).build();

                try (PreparedStatement psFecha = conexion.prepareStatement(
                        "SELECT max(fecha) FROM pago WHERE empresa = ?"
                )) {
                    psFecha.setString(1, participacion.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
                    ResultSet rsFecha = psFecha.executeQuery();
                    if (rsFecha.next()) {
                        participacion.getEmpresa().setFechaUltimoPago(rsFecha.getTimestamp(1));
                    }
                }

                participaciones.add(participacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participaciones;
    }

    public List<Participacion> getParticipacionesPorUsuario(String nombreUsuario, int numero) {
        List<Participacion> participaciones = new ArrayList<>();
        int limit = numero;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM participacion where usuario = ? limit ?"
        )) {
            ps.setString(1, nombreUsuario);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                participaciones.add(Mapeador.map(rs, Participacion.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participaciones;
    }

    public List<Participacion> getParticipacionesPorEmpresa(String nombreEmpresa){
        List<Participacion> participaciones = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT p.* FROM participacion as p WHERE p.empresa = ? AND p.empresa NOT LIKE p.usuario"
        )) {
            ps.setString(1, nombreEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                participaciones.add(Mapeador.map(rs, Participacion.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(nombreEmpresa);
        System.out.println(participaciones);
        return participaciones;
    }

}
