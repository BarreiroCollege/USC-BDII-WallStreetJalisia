package gal.sdc.usc.wallstreet.repository.helpers;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
import gal.sdc.usc.wallstreet.model.UsuarioTipo;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.EstadisticasDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.PagoDAO;
import gal.sdc.usc.wallstreet.repository.PagoUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.PropuestaCompraDAO;
import gal.sdc.usc.wallstreet.repository.ReguladorDAO;
import gal.sdc.usc.wallstreet.repository.SociedadDAO;
import gal.sdc.usc.wallstreet.repository.SuperUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.VentaDAO;

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
 *
 * @author Diego Barreiro [diego@barreiro.xyz]
 */
public abstract class DatabaseLinker {
    public static boolean DEBUG = false;
    // Indica que la conexión se ha inicializado
    private static Connection conexion;
    // Lista de DAOs disponibles
    private static HashMap<Class<? extends DAO<? extends Entidad>>, DAO<? extends Entidad>> daos;
    // Nivel de aislamiento por defecto
    private static int nivelAislamiento = -1;
    private static UsuarioSesion usuario;

    static {
        // Inicializar el hashmap
        DatabaseLinker.daos = new HashMap<>();
    }

    public DatabaseLinker() {
        if (conexion == null) cargarLinker();
    }

    /**
     * Devuelve un DAO que haya sido inicializado
     *
     * @param clase clase del DAO a buscar
     * @param <D>   DAO de salida
     * @return DAO instanciado
     */
    public static <D extends DAO<? extends Entidad>> D getSDAO(Class<D> clase) {
        return (D) DatabaseLinker.daos.get(clase);
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
            DatabaseLinker.nivelAislamiento = conexion.getTransactionIsolation();
        } catch (IOException | SQLException f) {
            f.printStackTrace();
        }
    }

    /**
     * Carga todos los DAO en el HashMap
     *
     * @param conexion conexión con la base de datos
     */
    private void cargarDAOs(Connection conexion) {
        try {
            // Analizar el paquete con las clases
            // TODO: Esto no funca en el NetBeans de los profes
            // Class<?>[] clases = PackageScanner.getClasses();
            Class<?>[] clases = new Class<?>[]{
                    EmpresaDAO.class,
                    EstadisticasDAO.class,
                    InversorDAO.class,
                    OfertaVentaDAO.class,
                    PagoDAO.class,
                    PagoUsuarioDAO.class,
                    ParticipacionDAO.class,
                    PropuestaCompraDAO.class,
                    ReguladorDAO.class,
                    SociedadDAO.class,
                    SuperUsuarioDAO.class,
                    UsuarioDAO.class,
                    VentaDAO.class
            };
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
     *
     * @param clase clase del DAO a buscar
     * @param <D>   DAO de salida
     * @return DAO instanciado
     */
    public <D extends DAO<? extends Entidad>> D getDAO(Class<D> clase) {
        return DatabaseLinker.getSDAO(clase);
    }

    /**
     * Indica si hay una sesión iniciada
     *
     * @return true cuando hay un usuario dentro
     */
    public boolean haySesion() {
        return usuario != null;
    }

    /**
     * Indica el tipo de usuario, si es inversor o empresa
     *
     * @return INVERSOR cuando es inversor, EMPRESA si es empresa, null si no hay sesión
     */
    public UsuarioTipo getTipoUsuario() {
        if (!haySesion()) return null;

        if (usuario instanceof Inversor) return UsuarioTipo.INVERSOR;
        else if (usuario instanceof Empresa) return UsuarioTipo.EMPRESA;
        return UsuarioTipo.REGULADOR;
    }

    /**
     * Devuelve el usuario si hay sesión
     *
     * @return Usuario
     */
    public UsuarioSesion getUsuarioSesion() {
        return DatabaseLinker.usuario;
    }

    /**
     * Inicia sesión
     *
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
     * Inicia una transacción con el nivel de aislamiento por defecto
     */
    public void iniciarTransaccion() {
        iniciarTransaccion(DatabaseLinker.nivelAislamiento);
    }

    /**
     * Inicia una transacción con el nivel de aislamiento especificado
     *
     * @param nivelAislamiento aislamiento
     */
    public void iniciarTransaccion(int nivelAislamiento) {
        try {
            DatabaseLinker.conexion.setAutoCommit(false);
            DatabaseLinker.conexion.setTransactionIsolation(nivelAislamiento);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ejecuta una transacción pendiente
     *
     * @return true cuando se ejecuta correctamente, false en caso contrario (rollback)
     */
    public boolean ejecutarTransaccion() {
        try {
            // Solo ejecutar si es commit manual
            if (!DatabaseLinker.conexion.getAutoCommit()) {
                DatabaseLinker.conexion.commit();
                DatabaseLinker.conexion.setAutoCommit(true);
                DatabaseLinker.conexion.setTransactionIsolation(DatabaseLinker.nivelAislamiento);

                return true;
            }
        } catch (SQLException e) {
            try {
                System.err.println(e.getMessage());
                DatabaseLinker.conexion.rollback();
                DatabaseLinker.conexion.setAutoCommit(true);
                DatabaseLinker.conexion.setTransactionIsolation(DatabaseLinker.nivelAislamiento);
            } catch (SQLException e2) {
                System.err.println(e2.getMessage());
            }
        }

        return false;
    }
}