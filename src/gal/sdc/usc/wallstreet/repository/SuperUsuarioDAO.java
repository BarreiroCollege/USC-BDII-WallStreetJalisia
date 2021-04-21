package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class SuperUsuarioDAO extends DAO<SuperUsuario> {
    public SuperUsuarioDAO(Connection conexion) {
        super(conexion, SuperUsuario.class);
    }
}
