package gal.sdc.usc.wallstreet.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseLinker {
    private static boolean cargado = false;
    private static HashMap<Class<? extends DAO>, DAO> daos;

    public DatabaseLinker() {
        if (!DatabaseLinker.cargado) cargarLinker();
    }

    private void cargarLinker() {
        Properties configuracion = new Properties();
        DatabaseLinker.daos = new HashMap<>();

        try (FileInputStream arqConfiguracion = new FileInputStream("db.properties")) {
            configuracion.load(arqConfiguracion);
            Properties usuario = new Properties();
            String gestor = configuracion.getProperty("engine");
            usuario.setProperty("user", configuracion.getProperty("user"));
            usuario.setProperty("password", configuracion.getProperty("pass"));

            Connection conexion = DriverManager.getConnection("jdbc:" + gestor + "://" +
                            configuracion.getProperty("host") + ":" +
                            configuracion.getProperty("port") + "/" +
                            configuracion.getProperty("dbname"),
                    usuario);

            cargarDAOs(conexion);
            DatabaseLinker.cargado = true;
        } catch (IOException | SQLException f) {
            System.out.println(f.getMessage());
        }
    }

    private void cargarDAOs(Connection conexion) {
        try {
            Class<?>[] clases = PackageScanner.getClasses();
            for (Class<?> clase : clases) {
                Class<? extends DAO> claseDao = (Class<? extends DAO>) clase;
                Constructor<?> ctor = claseDao.getConstructor(Connection.class);
                DAO object = (DAO) ctor.newInstance(conexion);
                DatabaseLinker.daos.put(claseDao, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DAO getDAO(Class<? extends DAO> clase) {
        return DatabaseLinker.daos.get(clase);
    }
}
