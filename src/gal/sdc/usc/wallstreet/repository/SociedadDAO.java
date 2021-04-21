package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class SociedadDAO extends DAO<Sociedad> {
    public SociedadDAO(Connection conexion) {
        super(conexion, Sociedad.class);
    }
}
