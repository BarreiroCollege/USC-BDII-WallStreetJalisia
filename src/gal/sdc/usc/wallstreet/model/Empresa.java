package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.repository.EmpresaDAO;

import java.util.Objects;

@Tabla("empresa")
public class Empresa implements Entidad {
    @Columna(value = "", pk = true)
    private Usuario usuario;

    @Columna("cif")
    private String cif;

    @Columna("nombre")
    private String nombre;

    private Empresa() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String dni) {
        this.cif = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empresa empresa = (Empresa) o;
        return usuario.equals(empresa.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario);
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "usuario=" + usuario +
                ", cif='" + cif + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    public static class Builder {
        private final Empresa empresa = new Empresa();

        public Builder() {
        }

        public Builder(Usuario usuario) {
            empresa.usuario = usuario;
        }

        public Builder withUsuario(Usuario usuario) {
            empresa.usuario = usuario;
            return this;
        }

        public Builder withCif(String cif) {
            empresa.cif = cif;
            return this;
        }

        public Builder withNombre(String nombre) {
            empresa.nombre = nombre;
            return this;
        }

        public Empresa build() {
            return empresa;
        }
    }
}
