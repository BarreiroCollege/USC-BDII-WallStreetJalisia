package gal.sdc.usc.wallstreet.util;

import gal.sdc.usc.wallstreet.model.Columna;
import gal.sdc.usc.wallstreet.model.Entidad;
import gal.sdc.usc.wallstreet.model.Tabla;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Clase base de los Data Access Object a la base de datos.
 * Contiene métodos genéricos de transacciones DML para el
 * uso sencillo de las entidades.
 * @param <E> {@link Entidad} sobre la que trabaja
 */
public abstract class DAO<E extends Entidad> {
    // Conexión a la base de datos
    protected final Connection conexion;
    // Clase de la entidad sobre la que trabaja
    private final Class<E> claseEntidad;

    protected DAO(Connection conexion, Class<E> claseEntidad) {
        this.conexion = conexion;
        this.claseEntidad = claseEntidad;
    }

    /**
     * Resuelve recursivamente las claves foráneas devolviéndolas en un HashMap
     * apto para una consulta.
     * @param e sub-entidad sobre la que trabajar
     * @return HashMap cuya clave es columna en SQL y valor el valor en cuestión
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private HashMap<String, Object> resolverPksForaneas(Entidad e)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashMap<String, Object> paresPk = new HashMap<>();

        // Iterar sobre los atributos de la entidad
        for (Field field : e.getClass().getDeclaredFields()) {
            Class<?> type = field.getType();
            String name = field.getName();

            // Si no es clave primaria, saltar
            Columna columna = field.getDeclaredAnnotation(Columna.class);
            if (!columna.pk()) continue;

            // Llamar al getter del atributo para recuperar el valor al ser privado
            Method method = e.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
            Object valor = method.invoke(e);

            // Si sigue siendo una entidad, resolver recursivamente
            if (type.equals(Entidad.class)) {
                paresPk.putAll(resolverPks(valor));
            } else {
                // Sino, insertar en el hashmap
                paresPk.put(columna.value(), valor);
            }
        }

        return paresPk;
    }

    /**
     * Dada una serie de valores, emparejar con las columnas respectivas de la entidad
     * del DAO
     * @param valores serie de valores que actuan como clave primaria
     * @return HashMap con nombre de columna y valor apto para SQL
     */
    private HashMap<String, Object> resolverPks(Object... valores) {
        HashMap<String, Object> paresPk = new LinkedHashMap<>();

        // Preparar el iterador sobre los valores dados
        Iterator<Object> it = Arrays.asList(valores).iterator();
        // Iterar sobre los atributos de la entidad del DAO
        for (Field field : claseEntidad.getDeclaredFields()) {
            Class<?> type = field.getType();

            // Si no es clave primaria, saltar
            Columna columna = field.getDeclaredAnnotation(Columna.class);
            if (!columna.pk()) continue;

            // Obtener el valor respectivo dado como PK, y confirmar
            // que coincide con el debido en la entidad
            Object valor = it.next();
            if (!type.equals(valor.getClass())) {
                System.err.println("Los tipos no coinciden!");
                return null;
            }

            // En caso del atributo ser otra entidad, resolver recursivamente
            if (Entidad.class.isAssignableFrom(type)) {
                try {
                    paresPk.putAll(resolverPksForaneas((Entidad) valor));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Sino, insertar en el hashmap
                paresPk.put(columna.value(), valor);
            }
        }

        return paresPk;
    }

    /**
     * SELECT genérico del DAO
     * @param valores clave primaria de la entidad en la base de datos
     * @return entidad como objeto
     */
    public final E get(Object... valores) {
        E obj = null;

        // Extraer el nombre de la tabla de la entidad
        Tabla tabla = claseEntidad.getAnnotation(Tabla.class);
        StringBuilder SQL = new StringBuilder("SELECT * FROM " + tabla.value() + " WHERE ");

        // Resolver a pares cada valor dado con su respectivo nombre de columna
        HashMap<String, Object> paresPk = resolverPks(valores);

        // Para cada elemento en los pares, encadenar como nuevo elemento del WHERE
        assert paresPk != null;
        Iterator<String> itMapKey = paresPk.keySet().iterator();
        while (itMapKey.hasNext()) {
            SQL.append(itMapKey.next()).append("=?");
            if (itMapKey.hasNext()) {
                SQL.append(" AND ");
            }
        }

        // Preparar la consulta
        try (PreparedStatement ps = this.conexion.prepareStatement(SQL.toString())) {
            // Empezar en el parámetro 1, y empezar a iterar sobre los valores de los pares
            int i = 1;
            for (Object value : paresPk.values()) {
                // Dependiendo del tipo, insertar de una forma u otra
                if (value instanceof Float) {
                    ps.setFloat(i, (Float) value);
                } else if (value instanceof Integer) {
                    ps.setInt(i, (Integer) value);
                } else if (value instanceof Date) {
                    ps.setDate(i, (Date) value);
                } else if (value instanceof Boolean) {
                    ps.setBoolean(i, (Boolean) value);
                } else {
                    ps.setString(i, value.toString());
                }
                // Incrementar el contador del parámetro
                i++;
            }

            // Ejecutar
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Si existe algún valor, mapear automáticamente las columnas al objeto
                obj = Mapeador.map(rs, claseEntidad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Devolver el objeto si existe, sino nulo
        return obj;
    }

    public final boolean crear(E e) {
        return false;
    }

    public final boolean actualizar(E e) {
        return false;
    }

    public final boolean borrar(E e) {
        return false;
    }
}
