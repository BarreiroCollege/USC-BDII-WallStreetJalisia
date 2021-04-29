package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
