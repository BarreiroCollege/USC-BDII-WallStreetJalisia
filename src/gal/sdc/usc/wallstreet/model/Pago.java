package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Date;
import java.util.Objects;

@Tabla("pago")
public class Pago implements Entidad {
    @Columna(value = "fecha", pk = true)
    private Date fecha;

    @Columna(value = "empresa", pk = true)
    private Empresa empresa;

    @Columna("beneficio_por_participacion")
    private Float beneficioPorParticipacion;

    @Columna("fecha_anuncio")
    private Date fechaAnuncio = null;

    private Pago() {
    }

    public Date getFecha() {
        return fecha;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Float getBeneficioPorParticipacion() {
        return beneficioPorParticipacion;
    }

    public void setBeneficioPorParticipacion(Float beneficioPorParticipacion) {
        this.beneficioPorParticipacion = beneficioPorParticipacion;
    }

    public Date getFechaAnuncio() {
        return fechaAnuncio;
    }

    public void setFechaAnuncio(Date fechaAnuncio) {
        this.fechaAnuncio = fechaAnuncio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pago pago = (Pago) o;
        return fecha.equals(pago.fecha) &&
                empresa.equals(pago.empresa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, empresa);
    }

    @Override
    public String toString() {
        return "Pago{" +
                "fecha=" + fecha +
                ", empresa=" + empresa +
                ", beneficioPorParticipacion=" + beneficioPorParticipacion +
                ", fechaAnuncio=" + fechaAnuncio +
                '}';
    }

    public static class Builder {
        private final Pago pago = new Pago();

        public Builder() {
        }

        public Builder(Date fecha, Empresa empresa) {
            pago.fecha = fecha;
            pago.empresa = empresa;
        }

        public Builder withFecha(Date fecha) {
            pago.fecha = fecha;
            return this;
        }

        public Builder withEmpresa(Empresa empresa) {
            pago.empresa = empresa;
            return this;
        }

        public Builder withBeneficioPorParticipacion(Float beneficioPorParticipacion) {
            pago.beneficioPorParticipacion = beneficioPorParticipacion;
            return this;
        }

        public Builder withFechaAnuncio(Date fechaAnuncio) {
            pago.fechaAnuncio = fechaAnuncio;
            return this;
        }

        public Pago build() {
            return pago;
        }
    }
}
