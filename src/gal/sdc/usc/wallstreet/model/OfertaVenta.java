package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Date;
import java.util.Objects;

@Tabla("oferta_venta")
public class OfertaVenta implements Entidad {
    @Columna(value = "fecha", pk = true)
    private Date fecha = new Date();

    @Columna(value = "usuario", pk = true)
    private Usuario usuario;

    @Columna("empresa")
    private Empresa empresa;

    @Columna("num_participaciones")
    private Integer numParticipaciones;

    @Columna("precio_venta")
    private Float precioVenta;

    @Columna("confirmado")
    private Boolean confirmado = false;

    private OfertaVenta() {
    }

    public Date getFecha() {
        return fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Integer getNumParticipaciones() {
        return numParticipaciones;
    }

    public void setNumParticipaciones(Integer numParticipaciones) {
        this.numParticipaciones = numParticipaciones;
    }

    public Float getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Float precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Boolean getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(Boolean confirmado) {
        this.confirmado = confirmado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfertaVenta that = (OfertaVenta) o;
        return fecha.equals(that.fecha) &&
                usuario.equals(that.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, usuario);
    }

    @Override
    public String toString() {
        return "OfertaVenta{" +
                "fecha=" + fecha +
                ", usuario=" + usuario +
                ", empresa=" + empresa +
                ", numParticipaciones=" + numParticipaciones +
                ", precioVenta=" + precioVenta +
                ", confirmado=" + confirmado +
                '}';
    }

    public static class Builder {
        private final OfertaVenta ofertaVenta = new OfertaVenta();

        public Builder() {
        }

        public Builder(Date fecha, Usuario usuario) {
            ofertaVenta.fecha = fecha;
            ofertaVenta.usuario = usuario;
        }

        public Builder withFecha(Date fecha) {
            ofertaVenta.fecha = fecha;
            return this;
        }

        public Builder withUsuario(Usuario usuario) {
            ofertaVenta.usuario = usuario;
            return this;
        }

        public Builder withEmpresa(Empresa empresa) {
            ofertaVenta.empresa = empresa;
            return this;
        }

        public Builder withNumParticipaciones(Integer numParticipaciones) {
            ofertaVenta.numParticipaciones = numParticipaciones;
            return this;
        }

        public Builder withPrecioVenta(Float precioVenta) {
            ofertaVenta.precioVenta = precioVenta;
            return this;
        }

        public Builder withConfirmado(Boolean confirmado) {
            ofertaVenta.confirmado = confirmado;
            return this;
        }

        public OfertaVenta build() {
            return ofertaVenta;
        }
    }
}