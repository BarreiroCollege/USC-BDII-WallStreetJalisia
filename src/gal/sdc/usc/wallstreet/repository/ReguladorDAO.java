package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Regulador;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReguladorDAO extends DAO<Regulador> {
    public ReguladorDAO(Connection conexion) {
        super(conexion, Regulador.class);
    }

    public Regulador getRegulador() {
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * FROM regulador LIMIT 1"
        )) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Mapeador.map(rs, Regulador.class);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /***
     * Devuelve el saldo acumulado en la cuenta del regulador
     *
     * @return Entero con el saldo; null en caso de error.
     */
    public Integer getSaldoRegulador(){
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT saldo " +
                        "FROM regulador r JOIN usuario u ON r.usuario = u.identificador "
        )){
            ResultSet rs = ps.executeQuery();
            if (rs.)
        }


    }
}
