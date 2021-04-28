package gal.sdc.usc.wallstreet.model;

/**
 * Indica el estado de un Usuario.
 */
public enum UsuarioEstado {
    // Puede iniciar sesión, ya que tiene el atributo (fecha) alta nulo y (fecha) baja nulo
    ACTIVO,

    // Se ha registrado, tiene atributo de (fecha) alta no nulo
    PENDIENTE_ALTA,

    // Ha pedido la baja, tiene el atributo de (fecha) alta nulo y (fecha) no nulo
    PENDIENTE_BAJA,

    // Se ha tramitado la baja, (fecha) activo es no nulo y (fecha) baja es no nulo
    BAJA
}
