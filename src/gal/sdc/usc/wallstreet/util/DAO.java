package gal.sdc.usc.wallstreet.util;

import gal.sdc.usc.wallstreet.model.Columna;
import gal.sdc.usc.wallstreet.model.Entidad;
import gal.sdc.usc.wallstreet.model.Tabla;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public abstract class DAO<E extends Entidad> {
    protected final Connection conexion;
    private final Class<E> claseEntidad;

    protected DAO(Connection conexion, Class<E> claseEntidad) {
        this.conexion = conexion;
        this.claseEntidad = claseEntidad;
    }

    private HashMap<String, Object> resolverPksForaneas(Entidad e) throws IllegalAccessException {
        HashMap<String, Object> paresPk = new HashMap<>();

        for (Field field : e.getClass().getDeclaredFields()) {
            Class<?> type = field.getType();

            Columna columna = field.getDeclaredAnnotation(Columna.class);
            if (!columna.pk()) continue;

            if (type.equals(Entidad.class)) {
                paresPk.putAll(resolverPks((Entidad) field.get(e)));
            } else {
                paresPk.put(columna.value(), field.get(e));
            }
        }

        return paresPk;
    }

    private HashMap<String, Object> resolverPks(Object... valores) {
        HashMap<String, Object> paresPk = new LinkedHashMap<>();

        Iterator<Object> it = Arrays.asList(valores).iterator();
        for (Field field : claseEntidad.getDeclaredFields()) {
            Class<?> type = field.getType();

            Columna columna = field.getDeclaredAnnotation(Columna.class);
            if (!columna.pk()) continue;

            Object valor = it.next();
            if (!type.equals(valor.getClass())) {
                System.err.println("Los tipos no coinciden!");
                return null;
            }

            if (type.equals(Entidad.class)) {
                try {
                    paresPk.putAll(resolverPksForaneas((Entidad) valor));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                paresPk.put(columna.value(), valor);
            }
        }

        return paresPk;
    }

    public final E get(Object... valores) {
        E usuario = null;

        Tabla tabla = claseEntidad.getAnnotation(Tabla.class);
        StringBuilder SQL = new StringBuilder("SELECT * FROM " + tabla.value() + " WHERE ");

        HashMap<String, Object> paresPk = resolverPks(valores);

        Iterator<String> itMapKey = paresPk.keySet().iterator();
        while (itMapKey.hasNext()) {
            SQL.append(itMapKey.next()).append("=?");
            if (itMapKey.hasNext()) {
                SQL.append(" AND ");
            }
        }

        try (PreparedStatement ps = this.conexion.prepareStatement(SQL.toString())) {
            int i = 1;
            Iterator<Object> itMapValues = paresPk.values().iterator();
            while (itMapValues.hasNext()) {
                Object value = itMapValues.next();
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
                i++;
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usuario = Mapeador.map(rs, claseEntidad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
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
