package gal.sdc.usc.wallstreet.util;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase que castea automáticamente los resultados de JDBC a
 * objetos de Java.
 */
public class Mapeador extends DatabaseLinker {
    // Clase constructora de las entidades
    private final static String BUILDER_CLASS = "Builder";

    private final ResultSet rs;
    private final Class<?> claseObj;
    private final Object builder;

    private Mapeador(final ResultSet rs, Class<?> claseObj) {
        this.rs = rs;
        this.claseObj = claseObj;
        this.builder = crearBuilder();
    }

    private Object crearBuilder() {
        // Realizar una instancia de la inner-class Builder para ir creando la entidad
        Object builder = null;
        try {
            Constructor<?> ctor = Class.forName(claseObj.getName() + "$" + BUILDER_CLASS).getConstructor();
            builder = ctor.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return builder;
    }

    private Entidad resolverSubentidad(String nombre, Class<? extends Entidad> e) throws SQLException {
        try {
            Class<? extends DAO<? extends Entidad>> clase = (Class<? extends DAO<? extends Entidad>>)
                    Class.forName(
                            e.getPackage().getName().replace("model", "repository")
                                    + "." + e.getSimpleName() + "DAO"
                    );
            DAO<? extends Entidad> dao = super.getDAO(clase);

            List<Object> values = new LinkedList<>();
            for (Field field : e.getDeclaredFields()) {
                Class<?> type = field.getType();

                if (!field.isAnnotationPresent(Columna.class)) continue;
                Columna columna = field.getDeclaredAnnotation(Columna.class);
                if (!columna.pk()) continue;

                // TODO: Gestionar si más de 1 PK
                String nombreColumna = nombre;
                if (nombre.equals("pago")) {
                    nombreColumna = nombre + "_" + columna.value();
                }

                if (Entidad.class.isAssignableFrom(field.getType())) {
                    Entidad ent = resolverSubentidad(nombreColumna, (Class<? extends Entidad>) field.getType());
                    values.add(ent);
                } else {
                    if (type.equals(Integer.class)) {
                        values.add(rs.getInt(nombreColumna));
                    } else if (type.equals(Float.class)) {
                        values.add(rs.getFloat(nombreColumna));
                    } else if (type.equals(Double.class)) {
                        values.add(rs.getDouble(nombreColumna));
                    } else if (type.equals(Boolean.class)) {
                        values.add(rs.getBoolean(nombreColumna));
                    } else if (type.equals(Date.class)) {
                        values.add(rs.getTimestamp(nombreColumna));
                    } else {
                        values.add(rs.getString(nombreColumna));
                    }
                }
            }

            Object[] objects = new Object[values.size()];
            for (int i = 0; i < values.size(); i++) objects[i] = values.get(i);
            return dao.get(objects);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private Object castear() throws SQLException {
        for (Field field : claseObj.getDeclaredFields()) {
            Class<?> type = field.getType();
            String name = field.getName();
            Object value;

            if (!field.isAnnotationPresent(Columna.class)) continue;
            Columna columna = field.getDeclaredAnnotation(Columna.class);

            if (Entidad.class.isAssignableFrom(type)) {
                value = resolverSubentidad(columna.value(), (Class<? extends Entidad>) type);
            } else {
                if (type.equals(Integer.class)) {
                    value = rs.getInt(columna.value());
                } else if (type.equals(Float.class)) {
                    value = rs.getFloat(columna.value());
                } else if (type.equals(Double.class)) {
                    value = rs.getDouble(columna.value());
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
            return builder.getClass().getMethod("build").invoke(builder);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Dado un ResultSet, lo convertirá automáticamente a la clase de salida
     * resolviendo todas sus dependencias automáticamente.
     *
     * @param rs       datos de entrada de SQL
     * @param claseObj clase resultante
     * @param <D>      tipo de clase a sacar
     * @return clase entidad especificada
     * @throws SQLException
     */
    public static <D> D map(final ResultSet rs, Class<D> claseObj) throws SQLException {
        Mapeador mapeador = new Mapeador(rs, claseObj);
        return (D) mapeador.castear();
    }
}
