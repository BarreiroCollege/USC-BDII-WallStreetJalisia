package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Date;
import java.util.Objects;

@Tabla("pago")
public class Pago extends Entidad {
    @Columna(value = "fecha", pk = true)
    private Date fecha = new Date();

    @Columna(value = "empresa", pk = true)
    private Empresa empresa;

    @Columna("beneficio_por_participacion")
    private Float beneficioPorParticipacion = 0f;

    @Columna("participacion_por_participacion")
    private Float participacionPorParticipacion = 0f;

    @Columna("fecha_anuncio")
    private Date fechaAnuncio = null;

    @Columna("porcentaje_beneficio")
    private Float porcentajeBeneficio = 0f;

    @Columna("porcentaje_participacion")
    private Float porcentajeParticipacion = 0f;

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

    public Float getParticipacionPorParticipacion() {
        return participacionPorParticipacion;
    }

    public void setParticipacionPorParticipacion(Float participacionPorParticipacion) {
        this.participacionPorParticipacion = participacionPorParticipacion;
    }

    public Date getFechaAnuncio() {
        return fechaAnuncio;
    }

    public void setFechaAnuncio(Date fechaAnuncio) {
        this.fechaAnuncio = fechaAnuncio;
    }

    public Float getPorcentajeBeneficio() {
        return porcentajeBeneficio;
    }

    public void setPorcentajeBeneficio(Float porcentajeBeneficio) {
        this.porcentajeBeneficio = porcentajeBeneficio;
    }

    public Float getPorcentajeParticipacion() {
        return porcentajeParticipacion;
    }

    public void setPorcentajeParticipacion(Float porcentajeParticipacion) {
        this.porcentajeParticipacion = porcentajeParticipacion;
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
                ", participacionPorParticipacion=" + participacionPorParticipacion +
                ", fechaAnuncio=" + fechaAnuncio +
                ", porcentajeBeneficio=" + porcentajeBeneficio +
                ", porcentajeParticipacion=" + porcentajeParticipacion +
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

        public Builder withParticipacionPorParticipacion(Float participacionPorParticipacion) {
            pago.participacionPorParticipacion = participacionPorParticipacion;
            return this;
        }

        public Builder withFechaAnuncio(Date fechaAnuncio) {
            pago.fechaAnuncio = fechaAnuncio;
            return this;
        }

        public Builder withPorcentajeBeneficio(Float porcentajeBeneficio) {
            pago.porcentajeBeneficio = porcentajeBeneficio;
            return this;
        }

        public Builder withPorcentajeParticipacion(Float porcentajeParticipacion) {
            pago.porcentajeParticipacion = porcentajeParticipacion;
            return this;
        }

        public Pago build() {
            return pago;
        }
    }
}
