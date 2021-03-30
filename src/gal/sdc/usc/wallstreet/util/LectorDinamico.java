package gal.sdc.usc.wallstreet.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LectorDinamico {
    public static <E> E llamarGetter(String nombre, Object o) {
        try {
            Method method = o.getClass().getMethod("get" + nombre.substring(0, 1).toUpperCase() + nombre.substring(1));
            return (E) method.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
