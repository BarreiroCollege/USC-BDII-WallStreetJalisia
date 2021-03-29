package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Usuario;

import java.sql.Connection;

public class UsuarioDAO extends DAO<Usuario> {
    public UsuarioDAO(Connection conexion) {
        super(conexion, Usuario.class);
    }
}
