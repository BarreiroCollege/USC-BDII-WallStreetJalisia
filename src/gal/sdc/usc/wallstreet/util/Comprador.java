package gal.sdc.usc.wallstreet.util;

import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Regulador;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioComprador;
import gal.sdc.usc.wallstreet.model.Venta;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.ReguladorDAO;
import gal.sdc.usc.wallstreet.repository.SociedadDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.VentaDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;

import java.util.Date;
import java.util.List;

public class Comprador extends DatabaseLinker {
    private final UsuarioComprador u;
    private final List<OfertaVenta> ofertaVentas;
    private final Integer cantidad;

    private Comprador(UsuarioComprador u, List<OfertaVenta> ofertasVenta, Integer cantidad) {
        this.u = u;
        this.ofertaVentas = ofertasVenta;
        this.cantidad = cantidad;
    }

    public static Integer comprar(UsuarioComprador u, List<OfertaVenta> ofertasVenta, Integer cantidad) {
        Comprador c = new Comprador(u, ofertasVenta, cantidad);
        return c.comprar();
    }

    private Integer comprar() {
        // Variables de estado
        float saldo, precio;
        int acomprar, partPosibles, compradas = 0;

        //Recojemos los datos actualizados
        Regulador regulador = super.getDAO(ReguladorDAO.class).getRegulador();
        if (u instanceof Usuario) saldo = ((Usuario) u).getSaldoDisponible();
        else saldo = ((Sociedad) u).getSaldoComunal();
        acomprar = cantidad;

        // Compramos de las ofertas de más baratas a las más caras, ya ordenadas en la tabla
        for (OfertaVenta oferta : ofertaVentas) {
            // Si se compraron las solicitadas o no hay dinero para más se para el bucle
            if (acomprar == compradas) break;
            if ((partPosibles = (int) Math.floor(saldo / oferta.getPrecioVenta())) == 0) break;

            // Se elige el minimo entre las restantes en la oferta, las que quedan por comprar para cubrir el cupo
            // y las que se pueden comprar con el saldo actual
            partPosibles = Math.min(partPosibles, acomprar - compradas);
            partPosibles = Math.min(partPosibles, oferta.getRestantes());

            compradas += partPosibles;
            precio = partPosibles * oferta.getPrecioVenta();
            saldo -= precio;

            // Se inserta la venta en la BD, las 'restantes' en la oferta_venta se reducen con un trigger
            super.getDAO(VentaDAO.class).insertar(new Venta.Builder().withCantidad(partPosibles)
                    .withOfertaVenta(oferta)
                    .withFecha(new Date(System.currentTimeMillis()))
                    .withUsuarioCompra(u.getSuperUsuario())
                    .build());

            // Aumentamos el saldo del vendedor (menos comision), que puede ser un Usuario o Sociedad
            Usuario usuario = super.getDAO(UsuarioDAO.class).seleccionar(oferta.getUsuario());
            if (usuario == null) { // Es sociedad
                Sociedad sociedad = super.getDAO(SociedadDAO.class).seleccionar(oferta.getUsuario());
                sociedad.setSaldoComunal(sociedad.getSaldoComunal() + precio * (1 - regulador.getComision()));
                super.getDAO(SociedadDAO.class).actualizar(sociedad);
            } else { // Es usuario
                usuario.setSaldo(usuario.getSaldo() + precio * (1 - regulador.getComision()));
                super.getDAO(UsuarioDAO.class).actualizar(usuario);
            }

            // Reducimos la cartera de participaciones del vendedor
            Participacion cartera = super.getDAO(ParticipacionDAO.class).seleccionar(oferta.getUsuario(), oferta.getEmpresa());
            cartera.setCantidad(cartera.getCantidad() - partPosibles);
            cartera.setCantidadBloqueada(cartera.getCantidadBloqueada() - partPosibles);
            super.getDAO(ParticipacionDAO.class).actualizar(cartera);

            // Le damos la comision al regulador
            regulador.getUsuario().setSaldo(regulador.getUsuario().getSaldo() + precio * regulador.getComision());
            super.getDAO(UsuarioDAO.class).actualizar(regulador.getUsuario());

            // Aumentamos la cartera de participaciones del comprador, si es la primera vez que compra se crea
            cartera = super.getDAO(ParticipacionDAO.class).seleccionar(u.getSuperUsuario(), oferta.getEmpresa());
            if (cartera != null) {
                cartera.setCantidad(cartera.getCantidad() + partPosibles);
                super.getDAO(ParticipacionDAO.class).actualizar(cartera);
            } else {
                cartera = new Participacion.Builder()
                        .withCantidad(partPosibles)
                        .withEmpresa(oferta.getEmpresa())
                        .withUsuario(u.getSuperUsuario())
                        .build();
                super.getDAO(ParticipacionDAO.class).insertar(cartera);
            }
        }

        // Reducimos el saldo del comprador
        if (u instanceof Usuario) {
            Usuario us = (Usuario) u;
            us.setSaldo(saldo + us.getSaldoBloqueado());
            super.getDAO(UsuarioDAO.class).actualizar(us);
        } else {
            Sociedad so = (Sociedad) u;
            so.setSaldoComunal(saldo + so.getSaldoComunal());
            super.getDAO(SociedadDAO.class).actualizar(so);
        }

        // Devolvemos el numero de participaciones compradas
        return compradas;
    }
}
