package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Date;
import java.util.Objects;

@Tabla("propuesta_compra")
public class PropuestaCompra extends Entidad {
    @Columna(value = "sociedad", pk = true)
    private Sociedad sociedad;

    @Columna(value = "fecha_inicio", pk = true)
    private Date fechaInicio;

    @Columna("cantidad")
    private Integer cantidad;

    @Columna("precio_max")
    private Float precioMax;

    @Columna("empresa")
    private Empresa empresa;

    private PropuestaCompra() {
    }

    public Sociedad getSociedad() {
        return sociedad;
    }

    public void setSociedad(Sociedad sociedad) {
        this.sociedad = sociedad;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPrecioMax() {
        return precioMax;
    }

    public void setPrecioMax(Float precioMax) {
        this.precioMax = precioMax;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropuestaCompra that = (PropuestaCompra) o;
        return sociedad.equals(that.sociedad) &&
                fechaInicio.equals(that.fechaInicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sociedad, fechaInicio);
    }

    @Override
    public String toString() {
        return "PropuestaCompra{" +
                "sociedad=" + sociedad +
                ", fechaInicio=" + fechaInicio +
                ", cantidad=" + cantidad +
                ", precioMax=" + precioMax +
                ", empresa=" + empresa +
                '}';
    }

    public static class Builder {
        private final PropuestaCompra propuestaCompra = new PropuestaCompra();

        public Builder() {
        }

        public Builder(Sociedad sociedad, Date fechaInicio) {
            propuestaCompra.sociedad = sociedad;
            propuestaCompra.fechaInicio = fechaInicio;
        }

        public Builder withSociedad(Sociedad sociedad) {
            propuestaCompra.sociedad = sociedad;
            return this;
        }

        public Builder withFechaInicio(Date fechaInicio) {
            propuestaCompra.fechaInicio = fechaInicio;
            return this;
        }

        public Builder withCantidad(Integer cantidad) {
            propuestaCompra.cantidad = cantidad;
            return this;
        }

        public Builder withPrecioMax(Float precioMax) {
            propuestaCompra.precioMax = precioMax;
            return this;
        }

        public Builder withEmpresa(Empresa empresa) {
            propuestaCompra.empresa = empresa;
            return this;
        }

        public PropuestaCompra build() {
            return propuestaCompra;
        }
    }
}
