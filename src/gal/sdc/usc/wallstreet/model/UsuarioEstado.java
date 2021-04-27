package gal.sdc.usc.wallstreet.model;

/**
 * Indica el estado de un Usuario.
 */
public enum UsuarioEstado {
    // Puede iniciar sesión, ya que tiene el atributo alta no nulo
    ACTIVO,

    // Se ha registrado, tiene atributo de alta nulo
    PENDIENTE_ALTA,

    // Ha pedido la baja, tiene el atributo de activo no nulo y baja no nulo
    PENDIENTE_BAJA,

    // Se ha tramitado la baja, activo es nulo y baja es no nulo
    BAJA
}
