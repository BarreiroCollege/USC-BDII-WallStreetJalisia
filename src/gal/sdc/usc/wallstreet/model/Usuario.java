package gal.sdc.usc.wallstreet.model;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;
import gal.sdc.usc.wallstreet.util.auth.PasswordStorage;

import java.util.Date;
import java.util.Objects;

@Tabla("usuario")
public class Usuario extends Entidad implements UsuarioComprador {
    @Columna(value = "identificador", pk = true)
    private SuperUsuario superUsuario;

    @Columna("clave")
    private String clave;

    @Columna("direccion")
    private String direccion;

    @Columna("cp")
    private String cp;

    @Columna("localidad")
    private String localidad;

    @Columna("telefono")
    private Integer telefono;

    @Columna("saldo")
    private Float saldo = 0.f;

    @Columna("saldo_bloqueado")
    private Float saldoBloqueado = 0.f;

    @Columna("alta")
    private Date alta;

    @Columna("baja")
    private Date baja;

    @Columna("otp")
    private String otp;

    @Columna("sociedad")
    private Sociedad sociedad;

    @Columna("lider")
    private Boolean lider = false;

    private Usuario() {
    }

    public SuperUsuario getSuperUsuario() {
        return superUsuario;
    }

    public void setSuperUsuario(SuperUsuario superUsuario) {
        this.superUsuario = superUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        try {
            this.clave = PasswordStorage.crearHash(clave);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            System.err.println(e.getMessage());
        }
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

    public Date getAlta() {
        return alta;
    }

    public void setActivo(Date alta) {
        this.alta = alta;
    }

    public Date getBaja() {
        return baja;
    }

    public void setBaja(Date baja) {
        this.baja = baja;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Sociedad getSociedad() {
        return sociedad;
    }

    public void setSociedad(Sociedad sociedad) {
        this.sociedad = sociedad;
    }

    public Boolean getLider() {
        return lider;
    }

    public void setLider(Boolean lider) {
        this.lider = lider;
    }

    public UsuarioEstado getEstado() {
        // Alta almacena la fecha en la que se pide el alta en el sistema
        // (o en la que se acepta la baja en caso de estar de baja)
        if (alta != null) {
            if (baja == null) return UsuarioEstado.PENDIENTE_ALTA;
            return UsuarioEstado.BAJA;
        } else {
            if (baja == null) return UsuarioEstado.ACTIVO;
            else return UsuarioEstado.PENDIENTE_BAJA;
        }
    }

    public Float getSaldoDisponible() {
        return saldo - saldoBloqueado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return superUsuario.equals(usuario.superUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(superUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "superUsuario='" + superUsuario + '\'' +
                ", clave='" + clave + '\'' +
                ", direccion='" + direccion + '\'' +
                ", cp='" + cp + '\'' +
                ", localidad='" + localidad + '\'' +
                ", telefono=" + telefono +
                ", saldo=" + saldo +
                ", saldoBloqueado=" + saldoBloqueado +
                ", alta=" + alta +
                ", baja=" + baja +
                ", otp=" + otp +
                ", sociedad=" + sociedad +
                ", lider=" + lider +
                '}';
    }


    public static class Builder {
        private final Usuario usuario = new Usuario();

        public Builder() {
        }

        public Builder(SuperUsuario superUsuario) {
            usuario.superUsuario = superUsuario;
        }

        public Builder withSuperUsuario(SuperUsuario superUsuario) {
            usuario.superUsuario = superUsuario;
            return this;
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

        public Builder withAlta(Date alta) {
            usuario.alta = alta;
            return this;
        }

        public Builder withBaja(Date baja) {
            usuario.baja = baja;
            return this;
        }

        public Builder withOtp(String otp) {
            usuario.otp = otp;
            return this;
        }

        public Builder withSociedad(Sociedad sociedad) {
            usuario.sociedad = sociedad;
            return this;
        }

        public Builder withLider(Boolean lider) {
            usuario.lider = lider;
            return this;
        }

        public Usuario build() {
            return usuario;
        }
    }
}