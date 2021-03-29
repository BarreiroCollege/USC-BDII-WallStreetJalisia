package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("poseer_participacion")
public class PoseerParticipacion implements Entidad {
    @Columna(value = "usuario", pk = true)
    private Usuario usuario;

    @Columna(value = "empresa", pk = true)
    private Empresa empresa;

    @Columna("cantidad")
    private Integer cantidad = 0;

    private PoseerParticipacion() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoseerParticipacion that = (PoseerParticipacion) o;
        return usuario.equals(that.usuario) &&
                empresa.equals(that.empresa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, empresa);
    }

    @Override
    public String toString() {
        return "PoseerParticipacion{" +
                "usuario=" + usuario +
                ", empresa=" + empresa +
                ", cantidad=" + cantidad +
                '}';
    }

    public static class Builder {
        private final PoseerParticipacion poseerParticipacion = new PoseerParticipacion();

        public Builder() {
        }

        public Builder(Usuario usuario, Empresa empresa) {
            poseerParticipacion.usuario = usuario;
            poseerParticipacion.empresa = empresa;
        }

        public Builder withUsuario(Usuario usuario) {
            poseerParticipacion.usuario = usuario;
            return this;
        }

        public Builder withEmpresa(Empresa empresa) {
            poseerParticipacion.empresa = empresa;
            return this;
        }

        public Builder withCantidad(Integer cantidad) {
            poseerParticipacion.cantidad = cantidad;
            return this;
        }

        public PoseerParticipacion build() {
            return poseerParticipacion;
        }
    }
}
