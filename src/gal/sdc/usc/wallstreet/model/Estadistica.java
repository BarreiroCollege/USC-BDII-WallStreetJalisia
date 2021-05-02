package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("estadistica")
public class Estadistica extends Entidad {

    @Columna(value = "empresa", pk = true)
    private String identificadorEmpresa;

    @Columna("beneficio_medio")
    private Float beneficioMedio;

    @Columna("participaciones_medias")
    private Float participacionesMedias;

    @Columna("num_pagos_mes")
    private Integer numPagosMes;

    @Columna("precio_medio_mes")
    private Float precioMedioMes;

    private Estadistica() {
    }

    public String getIdentificadorEmpresa() {
        return identificadorEmpresa;
    }

    public Float getBeneficioMedio() {
        return beneficioMedio;
    }

    public void setBeneficioMedio(Float beneficioMedio) {
        this.beneficioMedio = beneficioMedio;
    }

    public Float getParticipacionesMedias() {
        return participacionesMedias;
    }

    public void setParticipacionesMedias(Float participacionesMedias) {
        this.participacionesMedias = participacionesMedias;
    }

    public Integer getNumPagosMes() {
        return numPagosMes;
    }

    public void setNumPagosMes(Integer numPagosMes) {
        this.numPagosMes = numPagosMes;
    }

    public Float getPrecioMedioMes() {
        return precioMedioMes;
    }

    public void setPrecioMedioMes(Float precioMedioMes) {
        this.precioMedioMes = precioMedioMes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estadistica)) return false;
        Estadistica that = (Estadistica) o;
        return Objects.equals(identificadorEmpresa, that.identificadorEmpresa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificadorEmpresa);
    }

    public static class Builder {
        private final Estadistica estadistica = new Estadistica();

        public Builder() {
        }

        public Builder(String idEmpresa){ estadistica.identificadorEmpresa = idEmpresa; }

        public Estadistica.Builder withEmpresa(String idEmpresa) {
            estadistica.identificadorEmpresa = idEmpresa;
            return this;
        }

        public Estadistica.Builder withBeneficioMedio(Float beneficioMedio){
            estadistica.beneficioMedio = beneficioMedio;
            return this;
        }

        public Estadistica.Builder withParticipacionesMedias(Float participacionesMedias){
            estadistica.participacionesMedias = participacionesMedias;
            return this;
        }

        public Estadistica.Builder withNumPagosMes(Integer numPagosMes){
            estadistica.numPagosMes = numPagosMes;
            return this;
        }

        public Estadistica.Builder withPrecioMedioMes(Float precioMedioMes){
            estadistica.precioMedioMes = precioMedioMes;
            return this;
        }

        public Estadistica build() {
            return estadistica;
        }
    }
}