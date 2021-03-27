package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.util.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO extends DAO {
    public UsuarioDAO(Connection conexion) {
        super(conexion);
        System.out.println("UsuarioDAO instanciado");
    }

    public Usuario getUsuarioPorIdentificador(String identificador) {
        Usuario usuario = null;

        try (PreparedStatement ps = super.conexion.prepareStatement("SELECT * FROM usuario WHERE identificador=?")) {
            ps.setString(1, identificador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usuario = Usuario.Builder(rs.getString("identificador"))
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }
}
