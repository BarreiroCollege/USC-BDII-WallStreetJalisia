package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Date;
import java.util.Objects;

@Tabla("compra")
public class Compra extends Entidad {
    @Columna(value = "fecha", pk = true)
    private Date fecha = new Date();

    @Columna(value = "ov", pk = true)
    private OfertaVenta ofertaVenta;

    @Columna(value = "usuario_compra", pk = true)
    private Usuario usuarioCompra;

    @Columna("cantidad")
    private Integer cantidad;

    @Columna("comision")
    private Float comision = 0.05f;

    private Compra() {
    }

    public Date getFecha() {
        return fecha;
    }

    public OfertaVenta getOfertaVenta() {
        return ofertaVenta;
    }

    public Usuario getUsuarioCompra() {
        return usuarioCompra;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Float getComision() {
        return comision;
    }

    public void setComision(Float comision) {
        this.comision = comision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compra compra = (Compra) o;
        return fecha.equals(compra.fecha) &&
                ofertaVenta.equals(compra.ofertaVenta) &&
                usuarioCompra.equals(compra.usuarioCompra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, ofertaVenta, usuarioCompra);
    }

    @Override
    public String toString() {
        return "Compra{" +
                "fecha=" + fecha +
                ", ofertaVenta=" + ofertaVenta +
                ", usuarioCompra=" + usuarioCompra +
                ", cantidad=" + cantidad +
                ", comision=" + comision +
                '}';
    }

    public static class Builder {
        private final Compra compra = new Compra();

        public Builder() {
        }

        public Builder(Date fecha, OfertaVenta ofertaVenta, Usuario usuarioCompra) {
            compra.fecha = fecha;
            compra.ofertaVenta = ofertaVenta;
            compra.usuarioCompra = usuarioCompra;
        }

        public Builder withFecha(Date fecha) {
            compra.fecha = fecha;
            return this;
        }

        public Builder withOfertaVenta(OfertaVenta ofertaVenta) {
            compra.ofertaVenta = ofertaVenta;
            return this;
        }

        public Builder withUsuarioCompra(Usuario usuarioCompra) {
            compra.usuarioCompra = usuarioCompra;
            return this;
        }

        public Builder withCantidad(Integer cantidad) {
            compra.cantidad = cantidad;
            return this;
        }

        public Builder withComision(Float comision) {
            compra.comision = comision;
            return this;
        }

        public Compra build() {
            return compra;
        }
    }
}
