package gal.sdc.usc.wallstreet.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación indicando una columna de la base de datos
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Columna {
    // Nombre de la columna
    String value();

    // Indica si es clave primaria
    boolean pk() default false;

    // Lista de columnas que referencian la clave foránea
    String[] fk() default {};
}
