package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Regulador;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class ReguladorDAO extends DAO<Regulador> {
    public ReguladorDAO(Connection conexion) {
        super(conexion, Regulador.class);
    }
}
