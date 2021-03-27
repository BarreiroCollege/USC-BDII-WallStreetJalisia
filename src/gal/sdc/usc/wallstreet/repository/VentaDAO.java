package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.util.DAO;

import java.sql.Connection;

public class VentaDAO extends DAO {
    public VentaDAO(Connection conexion) {
        super(conexion);
        System.out.println("VentasDAO instanciado");
    }
}
