package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Compra;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class CompraDAO extends DAO<Compra> {
    public CompraDAO(Connection conexion) {
        super(conexion, Compra.class);
    }
}
