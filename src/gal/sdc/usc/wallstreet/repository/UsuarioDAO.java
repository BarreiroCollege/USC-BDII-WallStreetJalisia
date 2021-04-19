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
        System.out.println("UsuarioDAO instanciado");
        // jola

        //super(conexion, Usuario.class);
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
    public Usuario getUsuario(String identificador){
        return getUsuarios().stream().filter( user -> identificador.equals( user.getIdentificador() ) ).findFirst().orElse(null);
    }

    public Float getSaldoDisponible(Usuario usr){
        Usuario res;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM usuario WHERE identificador=?"
        )) {
            ps.setString(1,usr.getIdentificador());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                res = (Mapeador.map(rs, Usuario.class));
                return res.getSaldo()-res.getSaldoBloqueado();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0f;
    }

}
