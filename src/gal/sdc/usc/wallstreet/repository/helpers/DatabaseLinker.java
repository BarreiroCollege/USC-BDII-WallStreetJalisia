package gal.sdc.usc.wallstreet.repository.helpers;

import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.util.PackageScanner;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Clase que actúa de enlace con la base de datos. Puede ser extendida por cualquier
 * clase al usar variables estáticas.
 */
public class DatabaseLinker {
    // Indica que la conexión se ha inicializado
    private static boolean cargado = false;
    // Lista de DAOs disponibles
    private static HashMap<Class<? extends DAO<? extends Entidad>>, DAO<? extends Entidad>> daos;

    public static boolean DEBUG = false;

    static {
        // Inicializar el hashmap
        DatabaseLinker.daos = new HashMap<>();
    }

    public DatabaseLinker() {
        if (!DatabaseLinker.cargado) cargarLinker();
    }

    /**
     * Función que inicializa la base de datos de la configuración
     */
    private void cargarLinker() {
        // Leer el archivo de configuración properties
        Properties configuracion = new Properties();
        try (FileInputStream arqConfiguracion = new FileInputStream("db.properties")) {
            configuracion.load(arqConfiguracion);

            DatabaseLinker.DEBUG = configuracion.getProperty("debug").toLowerCase().equals("true");

            // Cargar la configuración
            Properties usuario = new Properties();
            String gestor = configuracion.getProperty("engine");
            usuario.setProperty("user", configuracion.getProperty("user"));
            usuario.setProperty("password", configuracion.getProperty("pass"));

            Connection conexion = DriverManager.getConnection("jdbc:" + gestor + "://" +
                            configuracion.getProperty("host") + ":" +
                            configuracion.getProperty("port") + "/" +
                            configuracion.getProperty("dbname"),
                    usuario);

            // Inicializar todos los DAOs
            cargarDAOs(conexion);
            // Marcar como ya inicializado
            DatabaseLinker.cargado = true;
        } catch (IOException | SQLException f) {
            f.printStackTrace();
        }
    }

    /**
     * Carga todos los DAO en el HashMap
     * @param conexion conexión con la base de datos
     */
    private void cargarDAOs(Connection conexion) {
        try {
            // Analizar el paquete con las clases
            Class<?>[] clases = PackageScanner.getClasses();
            // E iterar
            for (Class<?> clase : clases) {
                // Castear la clase a un DAO
                Class<? extends DAO<? extends Entidad>> claseDao = (Class<? extends DAO<? extends Entidad>>) clase;
                // Obtener el tipo de constructor estándar
                Constructor<?> ctor = claseDao.getConstructor(Connection.class);
                // Invocar al constructor
                DAO<? extends Entidad> object = (DAO<? extends Entidad>) ctor.newInstance(conexion);
                // Insertar en el hashmap de DAOs
                DatabaseLinker.daos.put(claseDao, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve un DAO que haya sido inicializado
     * @param clase clase del DAO a buscar
     * @param <D> DAO de salida
     * @return DAO instanciado
     */
    public <D extends DAO<? extends Entidad>> D getDAO(Class<D> clase) {
        return DatabaseLinker.getSDAO(clase);
    }

    /**
     * Devuelve un DAO que haya sido inicializado
     * @param clase clase del DAO a buscar
     * @param <D> DAO de salida
     * @return DAO instanciado
     */
    public static <D extends DAO<? extends Entidad>> D getSDAO(Class<D> clase) {
        return (D) DatabaseLinker.daos.get(clase);
    }
}
