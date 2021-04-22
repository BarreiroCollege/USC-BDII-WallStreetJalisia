package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Inversor;
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

public class InversorDAO extends DAO<Inversor> {
    public InversorDAO(Connection conexion) {
        super(conexion, Inversor.class);
    }

    /***
     *
     * @return Devuelve una lista con los datos de todos aquellos inversores con solicitudes de registro pendientes
     */
    public List<Usuario> getInversoresPendientes() {
        List<Usuario> pendientes = new ArrayList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario u JOIN identificador i ON u.identificador = i.usuario " +
                        "WHERE u.activo is false"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Usuario usuario = Mapeador.map(rs, Usuario.class);
                Inversor inversor = new Inversor.Builder().withUsuario(usuario).withDni(rs.getString("dni"))
                        .withNombre(rs.getString("nombre"))
                        .withApellidos(rs.getString("apellidos")).build();
                pendientes.add(inversor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendientes;
    }
}
