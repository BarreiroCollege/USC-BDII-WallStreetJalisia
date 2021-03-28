package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.util.DAO;

import java.sql.Connection;

public class UsuarioDAO extends DAO<Usuario> {
    public UsuarioDAO(Connection conexion) {
        super(conexion, Usuario.class);
    }
}
