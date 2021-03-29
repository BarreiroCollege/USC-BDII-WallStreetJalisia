package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;

import java.util.Objects;

@Tabla("inversor")
public class Inversor implements Entidad {
    @Columna(value = "usuario", pk = true)
    private Usuario usuario;

    @Columna("dni")
    private String dni;

    @Columna("nombre")
    private String nombre;

    @Columna("apellidos")
    private String apellidos;

    private Inversor() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inversor inversor = (Inversor) o;
        return usuario.equals(inversor.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario);
    }

    @Override
    public String toString() {
        return "Inversor{" +
                "usuario=" + usuario +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                '}';
    }

    public static class Builder {
        private final Inversor inversor = new Inversor();

        public Builder() {
        }

        public Builder(Usuario usuario) {
            inversor.usuario = usuario;
        }

        public Builder withUsuario(Usuario usuario) {
            inversor.usuario = usuario;
            return this;
        }

        public Builder withDni(String dni) {
            inversor.dni = dni;
            return this;
        }

        public Builder withNombre(String nombre) {
            inversor.nombre = nombre;
            return this;
        }

        public Builder withApellidos(String apellidos) {
            inversor.apellidos = apellidos;
            return this;
        }

        public Inversor build() {
            return inversor;
        }
    }
}
