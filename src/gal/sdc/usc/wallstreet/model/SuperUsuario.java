package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("superusuario")
public class SuperUsuario extends Entidad {
    @Columna(value = "identificador", pk = true)
    private String identificador;

    private boolean tieneParticipaciones;

    private SuperUsuario() {
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public boolean tieneParticipaciones() {
        return tieneParticipaciones;
    }

    public void setTieneParticipaciones(boolean tieneParticipaciones) {
        this.tieneParticipaciones = tieneParticipaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperUsuario that = (SuperUsuario) o;
        return identificador.equals(that.identificador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }

    @Override
    public String toString() {
        return "SuperUsuario{" +
                "identificador='" + identificador + '\'' +
                '}';
    }

    public static class Builder {
        private final SuperUsuario superUsuario = new SuperUsuario();

        public Builder() {
        }

        public Builder(String identificador) {
            superUsuario.identificador = identificador;
        }

        public Builder withIdentificador(String identificador) {
            superUsuario.identificador = identificador;
            return this;
        }

        public SuperUsuario build() {
            return superUsuario;
        }
    }
}