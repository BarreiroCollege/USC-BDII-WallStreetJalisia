package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("regulador")
public class Regulador extends Entidad implements UsuarioSesion {
    @Columna(value = "usuario", pk = true)
    private Usuario usuario;

    @Columna("comision")
    private Float comision;

    @Columna("comision_sociedad")
    private Float comisionSociedad;

    private Regulador() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Float getComision() {
        return comision;
    }

    public void setComision(Float comision) {
        this.comision = comision;
    }

    public Float getComisionSociedad() {
        return comisionSociedad;
    }

    public void setComisionSociedad(Float comisionSociedad) {
        this.comisionSociedad = comisionSociedad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Regulador empresa = (Regulador) o;
        return usuario.equals(empresa.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario);
    }

    @Override
    public String toString() {
        return "Regulador{" +
                "usuario=" + usuario +
                ", comision='" + comision + '\'' +
                ", comisionSociedad='" + comisionSociedad + '\'' +
                '}';
    }

    public static class Builder {
        private final Regulador regulador = new Regulador();

        public Builder() {
        }

        public Builder(Usuario usuario) {
            regulador.usuario = usuario;
        }

        public Builder withUsuario(Usuario usuario) {
            regulador.usuario = usuario;
            return this;
        }

        public Builder withComision(Float comision) {
            regulador.comision = comision;
            return this;
        }

        public Builder withComisionSociedad(Float comisionSociedad) {
            regulador.comisionSociedad = comisionSociedad;
            return this;
        }

        public Regulador build() {
            return regulador;
        }
    }
}
