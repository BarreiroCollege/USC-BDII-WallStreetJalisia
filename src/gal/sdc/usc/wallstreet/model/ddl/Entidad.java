package gal.sdc.usc.wallstreet.model.ddl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
                    try {
                        Method method = this.getClass().getMethod("get" + nombre.substring(0, 1).toUpperCase() + nombre.substring(1));
                        Entidad valor = (Entidad) method.invoke(this);
                        pks += valor.getNumPks();
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    pks++;
                }
            }
        }
        return pks;
    }
}
