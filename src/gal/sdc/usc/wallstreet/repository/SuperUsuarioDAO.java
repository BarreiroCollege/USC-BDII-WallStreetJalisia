package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SuperUsuarioDAO extends DAO<SuperUsuario> {

    public SuperUsuarioDAO(Connection conexion) {
        super(conexion, SuperUsuario.class);
    }

    public boolean actualizarIdentificador(String antiguo, String nuevo) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE superusuario SET identificador=? WHERE identificador=?"
        )) {
            ps.setString(1, nuevo);
            ps.setString(2, antiguo);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public void eliminarSuperUsuario(String idUsuario){
        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM superusuario " +
                        "WHERE identificador = ?"
        )){
            ps.setString(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void eliminarSuperUsuarios(List<String> identificadores){
        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM superusuario " +
                        "WHERE identificador = ?"
        )){
            for (String identificador : identificadores){
                ps.setString(1, identificador);
                ps.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
