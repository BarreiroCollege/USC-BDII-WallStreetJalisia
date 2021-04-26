package gal.sdc.usc.wallstreet.repository;

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

    public double getSaldoUsuario(String identificador){
        double saldo = 0.0;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM usuario WHERE identificador = ?"
        )) {
            ps.setString(1, identificador);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                saldo = rs.getDouble("saldo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saldo;
    }

    public Usuario getUsuario(String identificador) {
        return getUsuarios().stream().filter(user -> identificador.equals(user.getSuperUsuario().getIdentificador())).findFirst().orElse(null);
    }

}
