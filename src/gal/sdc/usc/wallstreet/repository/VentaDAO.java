package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Venta;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import java.sql.Connection;

public class VentaDAO extends DAO<Venta> {
    public VentaDAO(Connection conexion) {
        super(conexion, Venta.class);
    }


}
