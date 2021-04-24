package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends DAO<Usuario> {
    public UsuarioDAO(Connection conexion) {
        super(conexion, Usuario.class);
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM usuario"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Usuario.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;

    }

    public Usuario getUsuario(String identificador) {
        return getUsuarios().stream().filter(user -> identificador.equals(user.getSuperUsuario().getIdentificador())).findFirst().orElse(null);
    }

    /***
     * Devuelve el número de usuarios que aún no están activos, pero lo han solicitado
     *
     * @return Número de usuarios inactivos; null en caso de error
     */
    public Integer getNumInactivos(){
        Integer inactivos = null;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT count(*) as inactivos " +
                        "FROM usuario " +
                        "WHERE activo is false"
        )) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                inactivos = rs.getInt("inactivos");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return inactivos;
    }

    /***
     * Devuelve el número de usuarios que han solicitado darse de baja
     *
     * @return Número de usuarios que quieren darse de baja; null en caso de error
     */
    public Integer getNumSolicitudesBaja(){
        Integer solicitudesBaja = null;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT count(*) as bajas " +
                        "FROM usuario " +
                        "WHERE baja is true"
        )) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                solicitudesBaja = rs.getInt("bajas");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return solicitudesBaja;
    }

    /***
     * Acepta un usuario en la aplicación (cambia el campo activo a true)
     *
     * @param id Identificador del usuario a activar
     */
    public void aceptarUsuario(String id){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET activo = true " +
                        "WHERE identificador = ?"
        )) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void aceptarUsuariosTodos(){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET activo = true " +
                        "WHERE activo = false "
        )) {
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void rechazarBaja(String id){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET baja = false " +
                        "WHERE identificador = ?"
        )){
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

/*    public Boolean aceptarBajasTodas(List<Usuario> empresasBaja, List<Usuario> inversoresBaja) {
        Boolean hayBajasRechazadas = null;

*//*        empresasBaja.forEach(empresa -> {
            try (PreparedStatement ps = conexion.prepareStatement(
                    "SELECT FROM participacion "
            ))
        });*//*

        inversoresBaja.forEach(inversor -> {
            try (PreparedStatement ps = conexion.prepareStatement(
                    ""
            ))
        });

        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM usuario " +
                        "WHERE baja is true"
        ))
    }*/
}
