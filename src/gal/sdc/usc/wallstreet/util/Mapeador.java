package gal.sdc.usc.wallstreet.util;

import gal.sdc.usc.wallstreet.model.Columna;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Mapeador {
    private final static String BUILDER_CLASS = "Builder";

    private Mapeador() {
    }

    public static <D> D map(final ResultSet rs, Class<D> claseObj) throws SQLException {
        Object builder;

        try {
            Constructor<?> ctor = Class.forName(claseObj.getName() + "$" + BUILDER_CLASS).getConstructor();
            builder = ctor.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        for (Field field : claseObj.getDeclaredFields()) {
            Class<?> type = field.getType();
            String name = field.getName();
            Object value = null;

            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Columna) {
                    String columna = ((Columna) annotation).value();
                    if (type.equals(Integer.class)) {
                        value = rs.getInt(columna);
                    } else if (type.equals(Float.class)) {
                        value = rs.getFloat(columna);
                    } else if (type.equals(Boolean.class)) {
                        value = rs.getBoolean(columna);
                    } else if (type.equals(Date.class)) {
                        value = rs.getDate(columna);
                    } else {
                        value = rs.getString(columna);
                    }
                }
            }

            try {
                Method with = builder.getClass().getMethod("with" + name.substring(0, 1).toUpperCase()
                        + name.substring(1), type);
                with.invoke(builder, value);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            Object result = builder.getClass().getMethod("build").invoke(builder);
            return (D) result;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
