package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.util.DAO;

import java.sql.Connection;

public class UsuarioDAO extends DAO {
    public UsuarioDAO(Connection conexion) {
        super(conexion);
        System.out.println("UsuarioDAO instanciado");
    }
}
