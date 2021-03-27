package gal.sdc.usc.wallstreet.util;

import java.sql.Connection;

public abstract class DAO {
    protected final Connection conexion;

    protected DAO(Connection conexion) {
        this.conexion = conexion;
    }
}
