package gal.sdc.usc.wallstreet.model;

/**
 * Interfaz que es implementada por cualquier tipo de usuario que puede iniciar sesión (Inversor o Empresa).
 */
public interface UsuarioSesion {

    /**
     * Devuelve el usuario con la sesión iniciada
     *
     * @return Usuario
     */
    Usuario getUsuario();
}
