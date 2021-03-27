package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.util.DAO;

import java.sql.Connection;

public class Pagos2DAO extends DAO {
    public Pagos2DAO(Connection conexion) {
        super(conexion);
        System.out.println("PagosDAO2 instanciado");
    }
}
