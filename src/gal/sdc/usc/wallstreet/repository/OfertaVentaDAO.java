package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class OfertaVentaDAO extends DAO<OfertaVenta> {
    public OfertaVentaDAO(Connection conexion) {
        super(conexion, OfertaVenta.class);
    }
}
