package gal.sdc.usc.wallstreet.util;

import gal.sdc.usc.wallstreet.model.Columna;
import gal.sdc.usc.wallstreet.model.Entidad;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Clase que castea automáticamente los resultados de JDBC a
 * objetos de Java.
 */
public class Mapeador extends DatabaseLinker {
    // Clase constructora de las entidades
    private final static String BUILDER_CLASS = "Builder";

    private Mapeador() {
    }

    /**
     * Dado un ResultSet, lo convertirá automáticamente a la clase de salida
     * resolviendo todas sus dependencias automáticamente.
     * @param rs datos de entrada de SQL
     * @param claseObj clase resultante
     * @param <D> tipo de clase a sacar
     * @return clase entidad especificada
     * @throws SQLException
     */
    public static <D> D map(final ResultSet rs, Class<D> claseObj) throws SQLException {
        // TODO
        Mapeador mapeador = new Mapeador();

        // Realizar una instancia de la inner-class Builder para ir creando la entidad
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

            if (Entidad.class.isAssignableFrom(type)) {
                try {
                    Class<? extends DAO> clase = (Class<? extends DAO>) Class.forName(
                            type.getPackage().getName().replace("model", "repository")
                                    + "." + type.getSimpleName() + "DAO"
                    );
                    DAO dao = mapeador.getDAO(clase);

                    for (Field subField : type.getDeclaredFields()) {
                        if (!field.isAnnotationPresent(Columna.class)) continue;
                        Columna columna = subField.getDeclaredAnnotation(Columna.class);

                        // TODO
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (!field.isAnnotationPresent(Columna.class)) continue;
                Columna columna = field.getDeclaredAnnotation(Columna.class);
                if (type.equals(Integer.class)) {
                    value = rs.getInt(columna.value());
                } else if (type.equals(Float.class)) {
                    value = rs.getFloat(columna.value());
                } else if (type.equals(Boolean.class)) {
                    value = rs.getBoolean(columna.value());
                } else if (type.equals(Date.class)) {
                    value = rs.getDate(columna.value());
                } else {
                    value = rs.getString(columna.value());
                }
            }

            try {
                Method with = builder.getClass().getMethod(
                        "with" + name.substring(0, 1).toUpperCase() + name.substring(1),
                        type);
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
