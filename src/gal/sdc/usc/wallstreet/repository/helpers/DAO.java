package gal.sdc.usc.wallstreet.repository.helpers;

import gal.sdc.usc.wallstreet.model.ddl.Columna;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.model.ddl.Tabla;
import gal.sdc.usc.wallstreet.util.LectorDinamico;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Clase base de los Data Access Object a la base de datos.
 * Contiene métodos genéricos de transacciones DML para el
 * uso sencillo de las entidades.
 *
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
     *
     * @param nombre    columna de la clase hija
     * @param e         sub-entidad sobre la que trabajar
     * @param subNombre no nulo cuando hay más de un nivel, contiene el nombre de la columna de
     *                  segundo nivel
     * @return HashMap cuya clave es columna en SQL y valor el valor en cuestión
     * @note Sólo se necesitan las claves primarias, ya que no se inserta el resto de atributos
     * en las entidades hijas.
     */
    private HashMap<String, Object> resolverPksForaneas(String nombre, Entidad e, String subNombre) {
        HashMap<String, Object> paresPk = new HashMap<>();

        if (e == null) return paresPk;

        // Iterar sobre los atributos de la entidad
        for (Field field : e.getClass().getDeclaredFields()) {
            Class<?> type = field.getType();
            String name = field.getName();

            // Si no es clave primaria, saltar
            Columna columna = field.getDeclaredAnnotation(Columna.class);
            if (!columna.pk()) continue;

            // Llamar al getter del atributo para recuperar el valor al ser privado
            Object valor = LectorDinamico.llamarGetter(name, e);

            // Si sigue siendo una entidad, resolver recursivamente
            if (Entidad.class.isAssignableFrom(type)) {
                paresPk.putAll(resolverPksForaneas(
                        nombre,
                        // Es normal que pueda ser null, ya que puede haber valores nulos en la base de datos
                        (Entidad) valor,
                        subNombre != null ? subNombre : (e.getNumPks() > 1 ? columna.value() : null)
                ));
            } else {
                // Sino, insertar en el hashmap
                // En caso de la clave primaria estar compuesta por un único atributo,
                // tomar su nombre como búsqueda; sino enlazar con _
                if (subNombre == null && e.getNumPks() == 1) {
                    paresPk.put(nombre, valor);
                } else {
                    paresPk.put(nombre + "_" + (subNombre != null ? subNombre : columna.value()), valor);
                }
            }
        }

        return paresPk;
    }

    /**
     * Dada una serie de valores, emparejar con las columnas respectivas de la entidad
     * del DAO
     *
     * @param ta      indica si se buscan claves primarias, no primarias, o todos los atributos
     * @param valores serie de valores que actuan como clave primaria
     * @return HashMap con nombre de columna y valor apto para SQL
     */
    private HashMap<String, Object> emparejarColumnas(TipoAtributo ta, Object... valores) {
        HashMap<String, Object> paresPk = new LinkedHashMap<>();

        // Preparar el iterador sobre los valores dados
        Iterator<Object> it = Arrays.asList(valores).iterator();
        // Iterar sobre los atributos de la entidad del DAO
        for (Field field : claseEntidad.getDeclaredFields()) {
            Class<?> type = field.getType();

            // Dependiendo del tipo de clave, decidir si saltar o no
            Columna columna = field.getDeclaredAnnotation(Columna.class);
            if (ta.equals(TipoAtributo.PK) && !columna.pk()) continue;
            else if (ta.equals(TipoAtributo.NO_PK) && columna.pk()) continue;

            // Obtener el valor respectivo dado como PK, y confirmar
            // que coincide con el debido en la entidad
            Object valor = it.next();
            if (valor != null && !type.isAssignableFrom(valor.getClass())) {
                System.err.println("Los tipos no coinciden!");
                return null;
            }

            // En caso del atributo ser otra entidad, resolver recursivamente
            if (Entidad.class.isAssignableFrom(type)) {
                try {
                    // Aunque valor pueda ser nulo, es correcto, ya que puede haber atributos
                    // nulos a insertar en SQL
                    paresPk.putAll(resolverPksForaneas(columna.value(), (Entidad) valor, null));
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
     * Asigna los valores de los pares de atributos con los placements de un PreparedStatement
     *
     * @param paresPk conjunto de clave valor
     * @param ps      PreparedStatement a introducir
     * @return posición del último parámetro
     * @throws SQLException
     */
    private int asignarValores(HashMap<String, Object> paresPk, PreparedStatement ps) throws SQLException {
        return this.asignarValores(paresPk, ps, 1);
    }

    /**
     * Asigna los valores de los pares de atributos con los placements de un PreparedStatement
     *
     * @param paresPk conjunto de clave valor
     * @param ps      PreparedStatement a introducir
     * @param i       parámetro de inicio
     * @return posición del último parámetro
     * @throws SQLException
     */
    private int asignarValores(HashMap<String, Object> paresPk, PreparedStatement ps, int i) throws SQLException {
        // Empezar en el parámetro 1, y empezar a iterar sobre los valores de los pares
        for (Object value : paresPk.values()) {
            // Dependiendo del tipo, insertar de una forma u otra
            if (DatabaseLinker.DEBUG) System.out.println(i + " -> " + value);
            if (value == null) {
                ps.setNull(i, Types.NULL);
            } else if (value instanceof Float) {
                ps.setFloat(i, (Float) value);
            } else if (value instanceof Double) {
                ps.setDouble(i, (Double) value);
            } else if (value instanceof Integer) {
                ps.setInt(i, (Integer) value);
            } else if (value instanceof Date) {
                ps.setTimestamp(i, new Timestamp(((Date) value).getTime()));
            } else if (value instanceof Boolean) {
                ps.setBoolean(i, (Boolean) value);
            } else {
                ps.setString(i, value.toString());
            }
            // Incrementar el contador del parámetro
            i++;
        }

        return i;
    }

    /**
     * Dada una entidad, extraer los valores de los atributos en un array
     *
     * @param e  entidad sobre a la que extraer los valores
     * @param ta indica si es inserción, actualización, búsqueda o eliminación (en caso de ser inserción,
     *           tratará de buscar recursivamente e insertar las clases foraneas inexistentes, y en el
     *           caso de actualización actualizará recursivamente)
     * @return lista de objetos que actuan como atributos
     */
    private Object[] extraerAtributos(E e, TipoActualizacion ta, TipoAtributo tat) {
        // Lista de atributos como objetos
        List<Object> atributos = new LinkedList<>();

        // Para cada field, comprobar si es una columna
        for (Field field : e.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Columna.class)) continue;
            Columna columna = field.getAnnotation(Columna.class);
            if (tat.equals(TipoAtributo.NO_PK) && columna.pk()) continue;

            try {
                // Extraer el valor del atributo
                String name = field.getName();
                Object o = LectorDinamico.llamarGetter(name, e);

                // Si el atributo es una entidad, analizar recursivamente
                if (Entidad.class.isAssignableFrom(field.getType()) && o != null) {
                    Entidad subEntidad = (Entidad) o;

                    // Extraer el DAO de la subentidad
                    Class<? extends DAO<Entidad>> clase = (Class<? extends DAO<Entidad>>)
                            Class.forName(
                                    subEntidad.getClass().getPackage().getName().replace("model", "repository")
                                            + "." + subEntidad.getClass().getSimpleName() + "DAO"
                            );
                    DAO<Entidad> subDao = DatabaseLinker.getSDAO(clase);

                    switch (ta) {
                        // En caso de estar realizando inserts, insertar recursivamente
                        case INSERT:
                            if (subDao.seleccionar(subDao.extraerAtributos(
                                    subEntidad,
                                    TipoActualizacion.SELECT,
                                    tat
                            )) == null) {
                                subDao.insertar(subEntidad);
                            }
                            break;
                        // En caso de estar realizando updates, actualizar recursivamente
                        case UPDATE:
                            subDao.actualizar(subEntidad);
                            break;
                        // En el resto de casos no hacer nada más
                        case DELETE:
                        case SELECT:
                        default:
                            break;
                    }
                }

                // Añadir atributo al array de salida
                atributos.add(o);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        // Pasar de lista a array
        Object[] valores = new Object[atributos.size()];
        for (int i = 0; i < atributos.size(); i++) valores[i] = atributos.get(i);

        return valores;
    }

    /**
     * SELECT genérico del DAO
     *
     * @param valores clave primaria de la entidad en la base de datos
     * @return entidad como objeto
     */
    public final E seleccionar(Object... valores) {
        // Si no es una tabla saltar
        if (!claseEntidad.isAnnotationPresent(Tabla.class)) return null;

        // Extraer el nombre de la tabla de la entidad
        Tabla tabla = claseEntidad.getAnnotation(Tabla.class);
        StringBuilder SQL = new StringBuilder("SELECT * FROM " + tabla.value() + " WHERE ");

        // Resolver a pares cada valor dado con su respectivo nombre de columna
        HashMap<String, Object> paresPk = emparejarColumnas(TipoAtributo.PK, valores);
        // Si no hay paresPk es porque algo ha pasado
        if (paresPk == null || paresPk.size() == 0) return null;

        // Para cada elemento en los pares, encadenar como nuevo elemento del WHERE
        Iterator<Map.Entry<String, Object>> itMapKey = paresPk.entrySet().iterator();
        while (itMapKey.hasNext()) {
            Map.Entry<String, Object> entry = itMapKey.next();
            SQL.append(entry.getKey()).append("=?");
            if (itMapKey.hasNext()) {
                SQL.append(" AND ");
            }
        }

        // Preparar la consulta
        if (DatabaseLinker.DEBUG) System.out.println(SQL);
        try (PreparedStatement ps = this.conexion.prepareStatement(SQL.toString())) {
            // Asignar los parámetros
            asignarValores(paresPk, ps);

            // Ejecutar
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Si existe algún valor, mapear automáticamente las columnas al objeto
                return Mapeador.map(rs, claseEntidad);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        // Devolver el objeto si existe, sino nulo
        return null;
    }

    /**
     * INSERT recursivo genérico para el DAO
     *
     * @param e entidad a insertar
     * @return true cuando se ha insertado correctamente
     */
    public final boolean insertar(E e) {
        // Si no es una tabla saltar
        if (!claseEntidad.isAnnotationPresent(Tabla.class)) return false;

        // Extraer el nombre de la tabla de la entidad
        Tabla tabla = claseEntidad.getAnnotation(Tabla.class);
        StringBuilder SQL = new StringBuilder("INSERT INTO " + tabla.value() + " (");

        // Resolver a pares cada valor dado con su respectivo nombre de columna
        HashMap<String, Object> paresPk = emparejarColumnas(
                TipoAtributo.TODOS,
                extraerAtributos(e, TipoActualizacion.INSERT, TipoAtributo.TODOS)
        );
        // Si no hay paresPk es porque algo ha pasado
        if (paresPk == null) return false;

        // Para cada elemento en los pares, encadenar como nuevo elemento del WHERE
        Iterator<Map.Entry<String, Object>> itMapKey = paresPk.entrySet().iterator();
        while (itMapKey.hasNext()) {
            Map.Entry<String, Object> entry = itMapKey.next();
            SQL.append(entry.getKey());
            if (itMapKey.hasNext()) {
                SQL.append(", ");
            }
        }
        SQL.append(") VALUES (");

        // Añadir los valores como wildcards
        Iterator<Object> itMapValue = paresPk.values().iterator();
        while (itMapValue.hasNext()) {
            itMapValue.next();
            SQL.append("?");
            if (itMapValue.hasNext()) {
                SQL.append(", ");
            }
        }
        SQL.append(")");

        // Preparar la consulta
        if (DatabaseLinker.DEBUG) System.out.println(SQL);
        try (PreparedStatement ps = this.conexion.prepareStatement(SQL.toString())) {
            // Asignar los parámetros
            asignarValores(paresPk, ps);

            // Ejecutar
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        // Devolver el objeto si existe, sino nulo
        return false;
    }

    /**
     * UPDATE recursivo genérico para el DAO
     *
     * @param e entidad a actualizar
     * @return true cuando se ha actualizado correctamente
     */
    public final boolean actualizar(E e) {
        // Si no es una tabla saltar
        if (!claseEntidad.isAnnotationPresent(Tabla.class)) return false;

        // Extraer el nombre de la tabla de la entidad
        Tabla tabla = claseEntidad.getAnnotation(Tabla.class);
        StringBuilder SQL = new StringBuilder("UPDATE " + tabla.value() + " SET ");

        // Resolver a pares cada valor dado con su respectivo nombre de columna
        HashMap<String, Object> paresAtributos = emparejarColumnas(
                TipoAtributo.TODOS,
                extraerAtributos(e, TipoActualizacion.UPDATE, TipoAtributo.TODOS)
        );

        // Si no hay paresPk es porque algo ha pasado
        if (paresAtributos == null) return false;

        // Para cada elemento en los pares de atributo, actualizar
        Iterator<Map.Entry<String, Object>> itMapKey = paresAtributos.entrySet().iterator();
        while (itMapKey.hasNext()) {
            Map.Entry<String, Object> entry = itMapKey.next();
            SQL.append(entry.getKey()).append("=?");
            if (itMapKey.hasNext()) {
                SQL.append(", ");
            }
        }

        // Condición de búsqueda
        SQL.append(" WHERE ");

        // Resolver a pares cada valor dado con su respectivo nombre de columna
        HashMap<String, Object> paresPk = emparejarColumnas(
                TipoAtributo.PK,
                extraerAtributos(e, TipoActualizacion.UPDATE, TipoAtributo.TODOS)
        );
        // Si no hay paresPk es porque algo ha pasado
        if (paresPk == null) return false;

        // Para cada elemento en los pares, encadenar como nuevo elemento del WHERE
        itMapKey = paresPk.entrySet().iterator();
        while (itMapKey.hasNext()) {
            Map.Entry<String, Object> entry = itMapKey.next();
            SQL.append(entry.getKey()).append("=?");
            if (itMapKey.hasNext()) {
                SQL.append(" AND ");
            }
        }

        // Preparar la consulta
        if (DatabaseLinker.DEBUG) System.out.println(SQL);
        try (PreparedStatement ps = this.conexion.prepareStatement(SQL.toString())) {
            // Asignar los parámetros (primero los atributos, y se da la última posición
            // para empezar a asignar las claves primarias)
            asignarValores(paresPk, ps, asignarValores(paresAtributos, ps));

            // Ejecutar
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        // Devolver false si hubo algún error
        return false;
    }

    /**
     * DELETE genérico para el DAO
     *
     * @param e entidad a eliminar
     * @return true cuando se ha eliminado
     */
    public final boolean eliminar(E e) {
        // Si no es una tabla saltar
        if (!claseEntidad.isAnnotationPresent(Tabla.class)) return false;

        // Extraer el nombre de la tabla de la entidad
        Tabla tabla = claseEntidad.getAnnotation(Tabla.class);
        StringBuilder SQL = new StringBuilder("DELETE FROM " + tabla.value() + " WHERE ");

        // Resolver a pares cada valor dado con su respectivo nombre de columna
        HashMap<String, Object> paresPk = emparejarColumnas(TipoAtributo.PK, extraerAtributos(e, TipoActualizacion.DELETE, TipoAtributo.TODOS));
        // Si no hay paresPk es porque algo ha pasado
        if (paresPk == null) return false;

        // Para cada elemento en los pares, encadenar como nuevo elemento del WHERE
        Iterator<Map.Entry<String, Object>> itMapKey = paresPk.entrySet().iterator();
        while (itMapKey.hasNext()) {
            Map.Entry<String, Object> entry = itMapKey.next();
            SQL.append(entry.getKey()).append("=?");
            if (itMapKey.hasNext()) {
                SQL.append(" AND ");
            }
        }

        // Preparar la consulta
        if (DatabaseLinker.DEBUG) System.out.println(SQL);
        try (PreparedStatement ps = this.conexion.prepareStatement(SQL.toString())) {
            // Asignar los parámetros
            asignarValores(paresPk, ps);

            // Ejecutar
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        // Devolver false si hubo algún error
        return false;
    }
}
