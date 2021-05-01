package gal.sdc.usc.wallstreet.model;

/**
 * Interfaz que es implementada por cualquier tipo de usuario que puede realizar compras (Usuario o Sociedad).
 */
public interface UsuarioComprador {
    /**
     * Devuelve el SuperUsuario asociado al comprador
     * @return SuperUsuario
     */
    SuperUsuario getSuperUsuario();
}
