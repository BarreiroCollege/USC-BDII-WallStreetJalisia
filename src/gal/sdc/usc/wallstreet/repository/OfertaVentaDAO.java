package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OfertaVentaDAO extends DAO<OfertaVenta> {


    public OfertaVentaDAO(Connection conexion) {
        super(conexion, OfertaVenta.class);
    }

    public List<OfertaVenta> getOfertasVenta(String empresa, Float precioMax) {
        List<OfertaVenta> ofertas = new ArrayList<>();
        try {
            String statement = "SELECT * FROM oferta_venta " +
                    "WHERE confirmado is true and empresa=? ";
            if (!precioMax.equals(0f)) statement += "and precio_venta<=? ";
            statement += "and restantes>0 ORDER BY precio_venta asc, fecha desc";

            PreparedStatement ps = conexion.prepareStatement(statement);
            ps.setString(1, empresa);
            if (!precioMax.equals(0f)) ps.setFloat(2, precioMax);
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

    public List<OfertaVenta> getOfertasVentaUsuario(String empresa, String usuario) {
        List<OfertaVenta> ofertas = new ArrayList<>();
        try {
            String statement = "SELECT * FROM oferta_venta WHERE usuario=? and restantes>0 ";
            if (empresa != null) statement += "and empresa = ?  ";
            statement += "ORDER BY precio_venta asc";

            PreparedStatement ps = conexion.prepareStatement(statement);
            ps.setString(1, usuario);
            if (empresa != null) ps.setString(2, empresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ofertas.add(Mapeador.map(rs, OfertaVenta.class));
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
                "SELECT o.fecha, o.empresa, e.nombre, e.cif, o.num_participaciones, o.precio_venta, o.restantes " +
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
                        .withRestantes(rs.getInt("restantes"))
                        .withPrecioVenta(rs.getFloat("precio_venta")).build();

                try (PreparedStatement psFecha = conexion.prepareStatement(
                        "SELECT max(fecha) FROM oferta_venta WHERE empresa = ?"
                )) {
                    psFecha.setString(1, ofertaventa.getEmpresa().getUsuario().getSuperUsuario().getIdentificador());
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
    public Integer getNumParticipacionesRestantes(OfertaVenta ov) {
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

    public List<OfertaVenta> getOfertasVentaPorUsuario(String nombreUsuario, int numero) {
        List<OfertaVenta> ofertaVenta = new ArrayList<>();
        int limit = numero;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM oferta_venta where usuario = ? and restantes>0 limit ?"
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

    /***
     * Devuelve los datos de todas aquellas ofertas que aún no han sido revisadas y aceptadas por el regulador.
     *
     * @return Lista de ofertas de venta que no están confirmadas; null en caso de error
     */
    public List<OfertaVenta> getOfertasPendientes() {
        List<OfertaVenta> ofertasPendientes = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * " +
                        "FROM oferta_venta " +
                        "WHERE confirmado is false"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ofertasPendientes.add(Mapeador.map(rs, OfertaVenta.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return ofertasPendientes;
    }

    /***
     * Acepta una oferta de venta; la vuelve activa en el mercado.
     *
     * @param ofertaVenta Oferta de venta a activar.
     */
    public void aceptarOfertaVenta(OfertaVenta ofertaVenta) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE oferta_venta " +
                        "SET confirmado = true " +
                        "WHERE fecha = ? AND usuario = ?"
        )) {
            ps.setTimestamp(1, new Timestamp(ofertaVenta.getFecha().getTime()));
            ps.setString(2, ofertaVenta.getUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * Pone como activas a las ofertas de venta indicadas.
     *
     * @param pendientes Lista de ofertas de venta a ser activadas
     */
    public void aceptarOfertasVentaPendientes(List<OfertaVenta> pendientes) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE oferta_venta " +
                        "SET confirmado = true " +
                        "WHERE fecha = ? AND usuario = ?"
        )) {
            for (OfertaVenta oferta : pendientes) {
                ps.setTimestamp(1, new Timestamp(oferta.getFecha().getTime()));
                ps.setString(2, oferta.getUsuario().getIdentificador());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * Elimina una oferta de venta de la base de datos (no se acepta).
     *
     * @param ofertaVenta Oferta de venta a rechazar.
     */
    public void rechazarOfertaVenta(OfertaVenta ofertaVenta) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM oferta_venta " +
                        "WHERE fecha = ? AND usuario = ?"
        )) {
            ps.setTimestamp(1, new Timestamp(ofertaVenta.getFecha().getTime()));
            ps.setString(2, ofertaVenta.getUsuario().getIdentificador());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


