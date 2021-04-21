package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.PropuestaCompra;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class PropuestaCompraDAO extends DAO<PropuestaCompra> {
    public PropuestaCompraDAO(Connection conexion) {
        super(conexion, PropuestaCompra.class);
    }
}
