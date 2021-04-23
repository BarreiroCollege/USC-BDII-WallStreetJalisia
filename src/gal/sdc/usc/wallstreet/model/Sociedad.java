package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("sociedad")
public class Sociedad extends SuperUsuario {
    @Columna(value = "identificador", pk = true)
    private SuperUsuario identificador;

    @Columna("saldo_comuna")
    private Float saldoComunal = 0.0f;

    @Columna("tolerancia")
    private Integer tolerancia = 0;

    private Sociedad() {
    }

    protected SuperUsuario getSuper() {
        return super.getThis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sociedad sociedad = (Sociedad) o;
        return identificador.equals(sociedad.identificador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }

    @Override
    public String toString() {
        return "Sociedad{" +
                "identificador=" + identificador +
                ", saldoComunal=" + saldoComunal +
                ", tolerancia=" + tolerancia +
                '}';
    }

    public static class Builder {
        private final Sociedad sociedad = new Sociedad();

        public Builder() {
        }

        public Builder(SuperUsuario identificador) {
            sociedad.identificador = identificador;
            sociedad.getSuper().set(identificador);
        }

        public Builder withIdentificador(SuperUsuario identificador) {
            sociedad.identificador = identificador;
            sociedad.getSuper().set(identificador);
            return this;
        }

        public Builder withSaldoComunal(Float saldoComunal) {
            sociedad.saldoComunal = saldoComunal;
            return this;
        }

        public Builder withTolerancia(Integer tolerancia) {
            sociedad.tolerancia = tolerancia;
            return this;
        }

        public Sociedad build() {
            return sociedad;
        }
    }
}
