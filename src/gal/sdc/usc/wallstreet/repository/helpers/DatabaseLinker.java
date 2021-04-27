package gal.sdc.usc.wallstreet.repository.helpers;

import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.util.PackageScanner;
import gal.sdc.usc.wallstreet.model.UsuarioTipo;

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

    private static UsuarioSesion usuario;

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
        return usuario != null;
    }

    /**
     * Indica el tipo de usuario, si es inversor o empresa
     * @return INVERSOR cuando es inversor, EMPRESA si es empresa, null si no hay sesión
     */
    public UsuarioTipo getTipoUsuario() {
        return haySesion() ? (usuario instanceof Inversor ? UsuarioTipo.INVERSOR : UsuarioTipo.EMPRESA) : null;
    }

    /**
     * Devuelve el usuario si hay sesión
     * @return Usuario
     */
    public UsuarioSesion getUsuarioSesion() {
        return DatabaseLinker.usuario;
    }

    /**
     * Inicia sesión
     * @param usuario usuario
     */
    public void setUsuarioSesion(UsuarioSesion usuario) {
        DatabaseLinker.usuario = usuario;
    }

    /**
     * Cierra la sesión existente
     */
    public void cerrarSesion() {
        DatabaseLinker.usuario = null;
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
     * @return true cuando se ejecuta correctamente, false en caso contrario (rollback)
     */
    public boolean ejecutarTransaccion() {
        try {
            // Solo ejecutar si es commit manual
            if (!DatabaseLinker.conexion.getAutoCommit()) {
                DatabaseLinker.conexion.commit();
                DatabaseLinker.conexion.setAutoCommit(true);

                return true;
            }
        } catch (SQLException e) {
            try {
                System.err.println(e.getMessage());
                DatabaseLinker.conexion.rollback();
                DatabaseLinker.conexion.setAutoCommit(true);
            } catch (SQLException e2) {
                System.err.println(e2.getMessage());
            }
        }

        return false;
    }
}
