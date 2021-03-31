package gal.sdc.usc.wallstreet.model.ddl;

import gal.sdc.usc.wallstreet.util.LectorDinamico;

import java.lang.reflect.Field;

/**
 * Clase abstracta estándar de una entidad
 */
public abstract class Entidad {
    public final Integer getNumPks() {
        Integer pks = 0;
        for (Field field : this.getClass().getDeclaredFields()) {
            String nombre = field.getName();
            if (!field.isAnnotationPresent(Columna.class)) continue;

            if (field.getAnnotation(Columna.class).pk()) {
                if (Entidad.class.isAssignableFrom(field.getType())) {
                    Entidad valor = LectorDinamico.llamarGetter(nombre, this);
                    if (valor != null) {
                        pks += valor.getNumPks();
                    }
                } else {
                    pks++;
                }
            }
        }
        return pks;
    }
}
