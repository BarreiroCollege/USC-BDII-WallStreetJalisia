package gal.sdc.usc.wallstreet.model;

import java.util.Objects;

public class Usuario {
    private String identificador;
    private String clave;
    private String direccion;
    private String cp;
    private String localidad;
    private Integer telefono;
    private Float saldo;
    private Float saldoBloqueado = 0.f;
    private Boolean activo;

    private Usuario() {
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public Float getSaldo() {
        return saldo;
    }

    public void setSaldo(Float saldo) {
        this.saldo = saldo;
    }

    public Float getSaldoBloqueado() {
        return saldoBloqueado;
    }

    public void setSaldoBloqueado(Float saldoBloqueado) {
        this.saldoBloqueado = saldoBloqueado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return identificador.equals(usuario.identificador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "identificador='" + identificador + '\'' +
                ", clave='" + clave + '\'' +
                ", direccion='" + direccion + '\'' +
                ", cp='" + cp + '\'' +
                ", localidad='" + localidad + '\'' +
                ", telefono=" + telefono +
                ", saldo=" + saldo +
                ", saldoBloqueado=" + saldoBloqueado +
                ", activo=" + activo +
                '}';
    }

    public static class Builder {
        private final Usuario usuario = new Usuario();

        public Builder(String identificador) {
            usuario.identificador = identificador;
        }

        public Builder withClave(String clave) {
            usuario.clave = clave;
            return this;
        }

        public Builder withDireccion(String direccion) {
            usuario.direccion = direccion;
            return this;
        }

        public Builder withCp(String cp) {
            usuario.cp = cp;
            return this;
        }

        public Builder withLocalidad(String localidad) {
            usuario.localidad = localidad;
            return this;
        }

        public Builder withTelefono(Integer telefono) {
            usuario.telefono = telefono;
            return this;
        }

        public Builder withSaldo(Float saldo) {
            usuario.saldo = saldo;
            return this;
        }

        public Builder withSaldoBloqueado(Float saldoBloqueado) {
            usuario.saldoBloqueado = saldoBloqueado;
            return this;
        }

        public Builder withActivo(Boolean activo) {
            usuario.activo = activo;
            return this;
        }

        public Usuario build() {
            return usuario;
        }
    }
}
