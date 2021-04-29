package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
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

    public List<UsuarioSesion> getUsuariosPorSociedad(Sociedad s) {
        List<UsuarioSesion> usuarios = new ArrayList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT i.* FROM inversor i, usuario u WHERE i.usuario = u.identificador AND u.sociedad = ?"
        )) {
            ps.setString(1, s.getIdentificador().getIdentificador());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Inversor.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT e.* FROM empresa e, usuario u WHERE e.usuario = u.identificador AND u.sociedad = ?"
        )) {
            ps.setString(1, s.getIdentificador().getIdentificador());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Empresa.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return usuarios;
    }
}
