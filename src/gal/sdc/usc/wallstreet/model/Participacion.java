package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("participacion")
public class Participacion extends Entidad {
    @Columna(value = "usuario", pk = true)
    private SuperUsuario usuario;

    @Columna(value = "empresa", pk = true)
    private Empresa empresa;

    @Columna("cantidad")
    private Integer cantidad = 0;

    @Columna("cantidad_bloqueada")
    private Integer cantidadBloqueada = 0;

    private Participacion() {
    }

    public SuperUsuario getUsuario() {
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

    public Integer getCantidadBloqueada() {
        return cantidadBloqueada;
    }

    public void setCantidadBloqueada(Integer cantidadBloqueada) {
        this.cantidadBloqueada = cantidadBloqueada;
    }

    public Integer getCantidadDisponible() {
        return cantidad - cantidadBloqueada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participacion that = (Participacion) o;
        return usuario.equals(that.usuario) &&
                empresa.equals(that.empresa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, empresa);
    }

    @Override
    public String toString() {
        return "Participacion{" +
                "usuario=" + usuario +
                ", empresa=" + empresa +
                ", cantidad=" + cantidad +
                ", cantidadBloqueada=" + cantidadBloqueada +
                '}';
    }

    public static class Builder {
        private final Participacion participacion = new Participacion();

        public Builder() {
        }

        public Builder(SuperUsuario usuario, Empresa empresa) {
            participacion.usuario = usuario;
            participacion.empresa = empresa;
        }

        public Builder withUsuario(SuperUsuario usuario) {
            participacion.usuario = usuario;
            return this;
        }

        public Builder withEmpresa(Empresa empresa) {
            participacion.empresa = empresa;
            return this;
        }

        public Builder withCantidad(Integer cantidad) {
            participacion.cantidad = cantidad;
            return this;
        }

        public Builder withCantidadBloqueada(Integer cantidadBloqueada) {
            participacion.cantidadBloqueada = cantidadBloqueada;
            return this;
        }

        public Participacion build() {
            return participacion;
        }
    }
}
