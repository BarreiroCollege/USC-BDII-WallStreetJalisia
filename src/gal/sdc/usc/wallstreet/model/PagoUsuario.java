package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("pago_usuario")
public class PagoUsuario extends Entidad {
    @Columna(value = "usuario", pk = true)
    private SuperUsuario usuario;

    @Columna(value = "pago", pk = true)
    private Pago pago;

    @Columna("num_participaciones")
    private Integer numParticipaciones;

    private PagoUsuario() {
    }

    public SuperUsuario getUsuario() {
        return usuario;
    }

    public Pago getPago() {
        return pago;
    }

    public Integer getNumParticipaciones() {
        return numParticipaciones;
    }

    public void setNumParticipaciones(Integer numParticipaciones) {
        this.numParticipaciones = numParticipaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagoUsuario that = (PagoUsuario) o;
        return usuario.equals(that.usuario) &&
                pago.equals(that.pago);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, pago);
    }

    @Override
    public String toString() {
        return "PagoUsuario{" +
                "usuario=" + usuario +
                ", pago=" + pago +
                ", numParticipaciones=" + numParticipaciones +
                '}';
    }

    public static class Builder {
        private final PagoUsuario pagoUsuario = new PagoUsuario();

        public Builder() {
        }

        public Builder(SuperUsuario usuario, Pago pago) {
            pagoUsuario.usuario = usuario;
            pagoUsuario.pago = pago;
        }

        public Builder withUsuario(SuperUsuario usuario) {
            pagoUsuario.usuario = usuario;
            return this;
        }

        public Builder withPago(Pago pago) {
            pagoUsuario.pago = pago;
            return this;
        }

        public Builder withNumParticipaciones(Integer numParticipaciones) {
            pagoUsuario.numParticipaciones = numParticipaciones;
            return this;
        }

        public PagoUsuario build() {
            return pagoUsuario;
        }
    }
}
