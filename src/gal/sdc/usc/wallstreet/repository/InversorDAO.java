package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InversorDAO extends DAO<Inversor> {
    public InversorDAO(Connection conexion) {
        super(conexion, Inversor.class);
    }

    /***
     * Devuelve una lista con los datos de todas aquellos inversores con solicitudes de registro pendientes.
     *
     * @return Lista con los inverores que desean darse de alta.
     */
    public List<Inversor> getInversoresRegistrosPendientes() {
        List<Inversor> pendientes = new ArrayList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario u JOIN inversor i ON u.identificador = i.usuario " +
                        "WHERE u.alta is not null AND u.baja is null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Inversor inversor = Mapeador.map(rs, Inversor.class);
                pendientes.add(inversor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendientes;
    }

    /***
     * Devuelve todos los datos de un inversor (inversor y usuario) a partir de su identificador.
     *
     * @param identificador Clave primaria.
     * @return Inversor con todos sus datos.
     */
    public Inversor getInversor(String identificador) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * " +
                        "FROM inversor i JOIN usuario u ON i.usuario = u.identificador " +
                        "WHERE i.usuario = ?"
        )) {
            ps.setString(1, identificador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Mapeador.map(rs, Inversor.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * Devuelve una lista con los inversores que hayan solicitado una baja de la aplicación.
     *
     * @return Lista con los inversores que desean darse de baja.
     */
    public List<Inversor> getInversoresBajasPendientes() {
        List<Inversor> bajas = new ArrayList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario u JOIN inversor i ON u.identificador = i.usuario " +
                        "WHERE u.baja is not null AND u.alta is null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Inversor inversor = Mapeador.map(rs, Inversor.class);
                bajas.add(inversor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bajas;
    }
}
