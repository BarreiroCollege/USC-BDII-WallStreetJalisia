package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.util.DAO;

import java.sql.Connection;

public class InversorDAO extends DAO<Inversor> {
    public InversorDAO(Connection conexion) {
        super(conexion, Inversor.class);
    }
}
