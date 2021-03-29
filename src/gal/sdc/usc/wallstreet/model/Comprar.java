package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Date;
import java.util.Objects;

@Tabla("comprar")
public class Comprar {
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

    private Comprar() {
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
        Comprar comprar = (Comprar) o;
        return fecha.equals(comprar.fecha) &&
                ofertaVenta.equals(comprar.ofertaVenta) &&
                usuarioCompra.equals(comprar.usuarioCompra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, ofertaVenta, usuarioCompra);
    }

    @Override
    public String toString() {
        return "Comprar{" +
                "fecha=" + fecha +
                ", ofertaVenta=" + ofertaVenta +
                ", usuarioCompra=" + usuarioCompra +
                ", cantidad=" + cantidad +
                ", comision=" + comision +
                '}';
    }

    public static class Builder {
        private final Comprar comprar = new Comprar();

        public Builder() {
        }

        public Builder(Date fecha, OfertaVenta ofertaVenta, Usuario usuarioCompra) {
            comprar.fecha = fecha;
            comprar.ofertaVenta = ofertaVenta;
            comprar.usuarioCompra = usuarioCompra;
        }

        public Builder withFecha(Date fecha) {
            comprar.fecha = fecha;
            return this;
        }

        public Builder withOfertaVenta(OfertaVenta ofertaVenta) {
            comprar.ofertaVenta = ofertaVenta;
            return this;
        }

        public Builder withUsuarioCompra(Usuario usuarioCompra) {
            comprar.usuarioCompra = usuarioCompra;
            return this;
        }

        public Builder withCantidad(Integer cantidad) {
            comprar.cantidad = cantidad;
            return this;
        }

        public Builder withComision(Float comision) {
            comprar.comision = comision;
            return this;
        }

        public Comprar build() {
            return comprar;
        }
    }
}
