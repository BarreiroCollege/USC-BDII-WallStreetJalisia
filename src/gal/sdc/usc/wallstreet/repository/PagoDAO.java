package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Pago;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class PagoDAO extends DAO<Pago> {
    public PagoDAO(Connection conexion) {
        super(conexion, Pago.class);
    }
}
