package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO extends DAO<Empresa> {
    public EmpresaDAO(Connection conexion) {
        super(conexion, Empresa.class);
    }

    /***
     * Devuelve una lista con todas las empresas activas en la aplicación. Cada una de ellas contendrá todos los datos
     * de la tabla de empresa y la tabla de usuario.
     *
     * @return Lista de empresas dadas de alta.
     */
    public List<Empresa> getEmpresas() {
        List<Empresa> empresas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT e.* FROM empresa as e join usuario as u ON(e.usuario=u.identificador) WHERE alta is null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empresa empresa = Mapeador.map(rs, Empresa.class);
                empresas.add(empresa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empresas;
    }

    /***
     * Devuelve todos los datos de una empresa (empresa y usuario) a partir de su identificador.
     *
     * @param identificador Clave primaria.
     * @return Empresa con todos sus datos.
     */
    public Empresa getEmpresa(String identificador) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * " +
                        "FROM empresa e JOIN usuario u ON e.usuario = u.identificador " +
                        "WHERE e.usuario = ?"
        )) {
            ps.setString(1, identificador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Mapeador.map(rs, Empresa.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * Devuelve una lista con los datos de todas aquellas empresas con solicitudes de registro pendientes.
     *
     * @return Lista con las empresas que desean darse de alta.
     */
    public List<Empresa> getEmpresasRegistrosPendientes() {
        List<Empresa> pendientes = new ArrayList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario u JOIN empresa e ON u.identificador = e.usuario " +
                        "WHERE u.alta is not null AND u.baja is null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empresa empresa = Mapeador.map(rs, Empresa.class);
                pendientes.add(empresa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendientes;
    }

    /***
     * Devuelve una lista con las empresas que hayan solicitado una baja de la aplicación.
     *
     * @return Lista con las empresas que desean darse de baja.
     */
    public List<Empresa> getEmpresasBajasPendientes() {
        List<Empresa> bajas = new ArrayList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario u JOIN empresa e ON u.identificador = e.usuario " +
                        "WHERE u.baja is not null AND u.alta is null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empresa empresa = Mapeador.map(rs, Empresa.class);
                bajas.add(empresa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bajas;
    }
}
