package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;

import java.sql.Connection;

public class ParticipacionDAO extends DAO<Participacion> {
    public ParticipacionDAO(Connection conexion) {
        super(conexion, Participacion.class);
    }
}
