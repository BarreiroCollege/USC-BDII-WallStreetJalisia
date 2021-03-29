package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;

import java.sql.Connection;

public class EmpresaDAO extends DAO<Empresa> {
    public EmpresaDAO(Connection conexion) {
        super(conexion, Empresa.class);
    }
}
