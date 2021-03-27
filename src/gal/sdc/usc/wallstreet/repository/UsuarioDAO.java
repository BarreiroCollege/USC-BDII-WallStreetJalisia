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
                usuario = new Usuario.Builder(rs.getString("identificador"))
                        .withClave(rs.getString("clave"))
                        .withDireccion(rs.getString("direccion"))
                        .withCp(rs.getString("cp"))
                        .withLocalidad(rs.getString("localidad"))
                        .withTelefono(rs.getInt("telefono"))
                        .withSaldo(rs.getFloat("saldo"))
                        .withSaldoBloqueado(rs.getFloat("saldo_bloqueado"))
                        .withActivo(rs.getBoolean("activo"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }
}
