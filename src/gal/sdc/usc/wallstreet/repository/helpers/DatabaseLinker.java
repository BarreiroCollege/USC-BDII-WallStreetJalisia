package gal.sdc.usc.wallstreet.repository.helpers;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.util.TipoUsuario;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.util.PackageScanner;

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
    private static Connection conexion;
    // Lista de DAOs disponibles
    private static HashMap<Class<? extends DAO<? extends Entidad>>, DAO<? extends Entidad>> daos;

    private static Inversor inversor;
    private static Empresa empresa;

    public static boolean DEBUG = false;

    static {
        // Inicializar el hashmap
        DatabaseLinker.daos = new HashMap<>();
    }

    public DatabaseLinker() {
        if (conexion == null) cargarLinker();
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
            DatabaseLinker.conexion = conexion;
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

    /**
     * Indica si hay una sesión iniciada
     * @return true cuando hay un usuario dentro
     */
    public boolean haySesion() {
        return inversor != null || empresa != null;
    }

    /**
     * Indica el tipo de usuario, si es inversor o empresa
     * @return INVERSOR cuando es inversor, EMPRESA si es empresa, null si no hay sesión
     */
    public TipoUsuario getTipoUsuario() {
        return haySesion() ? (DatabaseLinker.inversor != null ? TipoUsuario.INVERSOR : TipoUsuario.EMPRESA) : null;
    }

    /**
     * Devuelve el usuario inversor si hay sesión
     * @return Inversor
     */
    public Inversor getInversor() {
        return DatabaseLinker.inversor;
    }

    /**
     * Devuelve el usuario empresa si hay sesión
     * @return Empresa
     */
    public Empresa getEmpresa() {
        return DatabaseLinker.empresa;
    }

    /**
     * Inicia sesión como inversor
     * @param inversor usuario
     */
    public void setInversor(Inversor inversor) {
        DatabaseLinker.inversor = inversor;
        DatabaseLinker.empresa = null;
    }

    /**
     * Inicia sesión como inversor
     * @param empresa usuario
     */
    public void setEmpresa(Empresa empresa) {
        DatabaseLinker.inversor = null;
        DatabaseLinker.empresa = empresa;
    }

    /**
     * Cierra la sesión existente
     */
    public void cerrarSesion() {
        DatabaseLinker.inversor = null;
        DatabaseLinker.empresa = null;
    }

    /**
     * Inicia una nueva transacción, deshabilitando el autocommit
     */
    public void iniciarTransaccion() {
        try {
            DatabaseLinker.conexion.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ejecuta una transacción pendiente
     */
    public void ejecutarTransaccion() {
        try {
            // Solo ejecutar si es commit manual
            if (!DatabaseLinker.conexion.getAutoCommit()) {
                DatabaseLinker.conexion.commit();
                DatabaseLinker.conexion.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
