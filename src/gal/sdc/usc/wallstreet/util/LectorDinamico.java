package gal.sdc.usc.wallstreet.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LectorDinamico {
    /**
     * Llama al método "getX" de una clase
     * @param nombre atributo a fijar
     * @param o objeto sobre el que fijar
     * @param <E> tipo de objeto
     * @return objeto actualizado
     */
    public static <E> E llamarGetter(String nombre, Object o) {
        try {
            Method method = o.getClass().getMethod(
                    "get" + nombre.substring(0, 1).toUpperCase() + nombre.substring(1)
            );
            return (E) method.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Llama al método "withX" de un Builder
     * @param nombre atributo a fijar
     * @param o objeto sobre el que fijar
     * @param tipo clase del atributo
     * @param valor objeto del atributo
     * @param <E> tipo de objeto
     * @return objeto actualizado
     */
    public static <E> E llamarWither(String nombre, Object o, Class<?> tipo, Object valor) {
        try {
            Method method = o.getClass().getMethod(
                    "with" + nombre.substring(0, 1).toUpperCase() + nombre.substring(1),
                    tipo
            );
            return (E) method.invoke(o, valor);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
