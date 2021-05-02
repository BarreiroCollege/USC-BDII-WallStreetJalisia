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
     * Devuelve uno de los atributos accesibles del regulador (usa una función).
     *
     * @param atributo Atributo a recuperar (identificador, saldo o comision)
     * @return Correspondiente valor en formato String. Null si el atributo es no válido o en caso de error.
     */
    public String getDatoRegulador(String atributo) {
        String valor = null;
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * FROM dato_regulador(?)"
        )) {
            ps.setString(1, atributo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                valor = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return valor;
    }
}
