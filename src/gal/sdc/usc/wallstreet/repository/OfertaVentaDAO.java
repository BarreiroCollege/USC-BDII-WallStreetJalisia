package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class OfertaVentaDAO extends DAO<OfertaVenta> {


    public OfertaVentaDAO(Connection conexion) {
        super(conexion, OfertaVenta.class);
    }

    public List<OfertaVenta> getOfertasVenta(String empresa, Float precioMax) {
        List<OfertaVenta> ofertas = new ArrayList<>();
        try{
            String statement = "SELECT * FROM oferta_venta " +
                                "WHERE confirmado is true and empresa=? ";
            if(!precioMax.equals(0f)) statement += "and precio_venta<=? ";
            statement += "and restantes>0 ORDER BY precio_venta asc";

            PreparedStatement ps = conexion.prepareStatement(statement);
            ps.setString(1,empresa);
            if(!precioMax.equals(0f)) ps.setFloat(2,precioMax);
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

    /**
     * Devuelve todas las ofertas de venta de un usuario indicado.
     *
     * @param u Usuario que posee las ofertas
     * @return List<OfertaVenta> con las ofertas correspondientes
     */
    public List<OfertaVenta> getOfertasVenta(Usuario u) {
        return getOfertasVenta(u.getSuperUsuario().getIdentificador());
    }

    /**
     * Devuelve todas las ofertas de venta a nombre de un usuario.
     *
     * @param idUsuario usuario que posee las ofertas
     * @return List<OfertaVenta> con las ofertas correspondientes
     */
    public List<OfertaVenta> getOfertasVenta(String idUsuario) {
        List<OfertaVenta> ofertas = new ArrayList<>();
        Usuario usuario = new Usuario.Builder(new SuperUsuario.Builder(idUsuario).build()).build();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT o.fecha, o.empresa, e.nombre, e.cif, o.num_participaciones, o.precio_venta " +
                        "FROM oferta_venta o JOIN empresa e ON o.empresa = e.usuario " +
                        "WHERE o.usuario = ?"
        )) {
            ps.setString(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OfertaVenta ofertaventa = new OfertaVenta.Builder()
                        .withFecha(rs.getTimestamp("fecha"))
                        .withUsuario(usuario.getSuperUsuario())
                        .withEmpresa(
                                new Empresa.Builder(
                                        new Usuario.Builder(new SuperUsuario.Builder(rs.getString("empresa")).build()).build()
                                )
                                        .withCif(rs.getString("cif"))
                                        .withNombre(rs.getString("nombre")).build()
                        )
                        .withNumParticipaciones(rs.getInt("num_participaciones"))
                        .withPrecioVenta(rs.getFloat("precio_venta")).build();

                try (PreparedStatement psFecha = conexion.prepareStatement(
                        "SELECT max(fecha) FROM oferta_venta WHERE empresa = ?"
                )) {
                    psFecha.setString(1, ofertaventa.getEmpresa().getUsuario().getSuperUsuario().getIdentificador() );
                    ResultSet rsFecha = psFecha.executeQuery();
                    if (rsFecha.next()) {
                        ofertaventa.getEmpresa().setFechaUltimoPago(rsFecha.getTimestamp(1));
                    }
                }

                ofertas.add(ofertaventa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ofertas;
    }

    /***
     * Devuelve el número de participaciones que todavía no han sido vendidas de una oferta de venta determinada.
     *
     * @param ov Oferta de venta sobre la que se calculará el total de participaciones restante.
     * @return Entero con el número de participaciones no vendidas; null en caso de error.
     */
    public Integer getNumParticipacionesRestantes(OfertaVenta ov){
        Integer sinVender = null;

        // TODO: restantes
        /* String sqlCall = "SELECT * FROM participaciones_por_vender (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sqlCall)){
            ps.setTimestamp(1, new Timestamp(ov.getFecha().getTime()));
            ps.setString(2, ov.getUsuario().getIdentificador());
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                sinVender = rs.getInt(1);
            }
        } catch (SQLException e){
            e.printStackTrace();
        } */

        return sinVender;
    }

    /***
     * Elimina una oferta de venta.
     *
     * @param ov Oferta de venta a dar de baja.
     */
    public void darDeBajaOferta(OfertaVenta ov){
        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM oferta_venta " +
                        "WHERE fecha = ? and usuario = ?")){
            ps.setTimestamp(1, new Timestamp(ov.getFecha().getTime()));
            ps.setString(2, ov.getUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<OfertaVenta> getOfertasVentaPorUsuario(String nombreUsuario, int numero) {
        List<OfertaVenta> ofertaVenta = new ArrayList<>();
        int limit = numero;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM oferta_venta where usuario = ? limit ?"
        )) {
            ps.setString(1, nombreUsuario);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ofertaVenta.add(Mapeador.map(rs, OfertaVenta.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ofertaVenta;
    }
}


