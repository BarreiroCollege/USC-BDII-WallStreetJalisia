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
        SuperUsuario usuario = new SuperUsuario.Builder(idUsuario).build();

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

    public Boolean tieneParticipaciones(Usuario usuario) {
        return tieneParticipaciones(usuario.getSuperUsuario().getIdentificador());
    }

    /***
     * Comprueba si un superusuario tiene participaciones, ya sea como poseedor (usuario) o como empresa de las
     * participaciones.
     *
     * @param idSuperUsuario Clave primaria.
     * @return true, si el superusuario tiene participaciones; false, si no las tiene; null, en caso de error.
     */
    public Boolean tieneParticipaciones(String idSuperUsuario) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT count(*) " +
                        "FROM participacion " +
                        "WHERE (usuario = ? OR empresa = ?) AND (cantidad != ? OR cantidad_bloqueada != ?)"
        )) {
            ps.setString(1, idSuperUsuario);
            ps.setString(2, idSuperUsuario);
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * Comprueba si un superusuario tiene participaciones de si mismo, como empresa de las participaciones.
     *
     * @param idSuperUsuario Clave primaria.
     * @return true, si el superusuario tiene participaciones; false, si no las tiene; null, en caso de error.
     */
    public Boolean tieneParticipacionesPropias(String idSuperUsuario) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT count(*)" +
                        " FROM participacion" +
                        " WHERE usuario = ? AND empresa = ?"
        )) {
            ps.setString(1, idSuperUsuario);
            ps.setString(2, idSuperUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getParticipacionesUsuarioEmpresa(String nombreUsuario, String empresa) {
        Integer participaciones = 0;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT participacion.cantidad -participacion.cantidad_bloqueada " +
                        "as cuenta FROM participacion where usuario = ? and empresa= ?"
        )) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, empresa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                participaciones = Integer.parseInt(rs.getString("cuenta"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participaciones;
    }

    /**
     * Permite obtener el numero de particicpaciones bloqueadas de un usuario con una empresa especifica
     *
     * @param nombreUsuario identificador del usuario a consultar
     * @param empresa       identificador de la empresa de la que se poseen participaciones
     * @return
     */
    public Integer getParticipacionesBloqueadasUsuarioEmpresa(String nombreUsuario, String empresa) {
        int participaciones = 0;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT cantidad_bloqueada FROM participacion where usuario = ? and empresa= ?"
        )) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, empresa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                participaciones = Integer.parseInt(rs.getString("cantidad_bloqueada"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participaciones;
    }

    public List<Participacion> getParticipacionesPorEmpresa(String nombreEmpresa) {
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
        return participaciones;
    }

}
