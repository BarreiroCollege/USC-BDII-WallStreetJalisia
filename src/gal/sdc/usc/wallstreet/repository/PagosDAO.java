package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.util.DAO;

import java.sql.Connection;

public class PagosDAO extends DAO {
    public PagosDAO(Connection conexion) {
        super(conexion);
        System.out.println("PagosDAO instanciado");
    }

    public void carga() {

    }
}
