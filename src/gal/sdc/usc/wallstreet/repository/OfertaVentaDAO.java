package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class OfertaVentaDAO extends DAO<OfertaVenta> {


    public OfertaVentaDAO(Connection conexion) {
        super(conexion, OfertaVenta.class);
    }

    public List<OfertaVenta> getOfertasVenta(String empresa, Float precioMax){
        List<OfertaVenta> ofertas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * FROM oferta_venta " +
                        "WHERE confirmado is true and empresa=? and precio_venta<=? and num_participaciones>0 " +
                        "ORDER BY precio_venta asc"
        )) {
            ps.setString(1,empresa);
            ps.setFloat(2,precioMax);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OfertaVenta oferta = Mapeador.map(rs, OfertaVenta.class);
                ofertas.add(oferta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ofertas;
    }

    public Integer getNumOfertasPendientes(){
        Integer ofertasPendientes = null;
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT count(*) as pendientes " +
                        "FROM oferta_venta " +
                        "WHERE confirmado is false"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ofertasPendientes = rs.getInt("pendientes");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return ofertasPendientes;
    }

    public List<OfertaVenta> getOfertasPendientes(){
        List<OfertaVenta> ofertasPendientes = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * " +
                        "FROM oferta_venta " +
                        "WHERE confirmado is false"
        )){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ofertasPendientes.add(Mapeador.map(rs, OfertaVenta.class));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return ofertasPendientes;
    }
}


