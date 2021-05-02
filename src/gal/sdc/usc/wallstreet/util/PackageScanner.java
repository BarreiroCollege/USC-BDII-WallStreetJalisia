package gal.sdc.usc.wallstreet.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Clase que escanea un paquete y devuelve todas sus clases. Tomado
 * originalmente de https://stackoverflow.com/a/520344/6626193 y modificado
 * para su funcionamiento en NetBeans.
 */
public class PackageScanner {
    private final static String DAO_PACKAGE = "gal.sdc.usc.wallstreet.repository";

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getClasses() throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = PackageScanner.DAO_PACKAGE.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory The base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory) throws ClassNotFoundException {
        List<String> names;
        if (directory.toString().startsWith("file:") && directory.toString().contains(".jar!")) {
            names = loadFromJar();
        } else {
            names = loadFromDir(directory);
        }

        List<Class<?>> classes = new ArrayList<>();
        for (String name : names) {
            classes.add(Class.forName(name));
        }
        return classes;
    }

    /**
     * Load a list of .class files from a JAR file
     *
     * @return list of class names
     */
    private static List<String> loadFromJar() {
        List<String> classes = new ArrayList<>();

        String s = new File(PackageScanner.class.getResource("").getPath()).getParent().replaceAll("(!|file:\\\\)", "");
        s = s.split(".jar")[0] + ".jar";

        try (JarFile jf = new JarFile(s)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.getName().startsWith(DAO_PACKAGE.replace(".", "/"))
                        && je.getName().endsWith(".class")
                        && !je.getName().replace(DAO_PACKAGE.replace(".", "/"), "").substring(1).contains("/")) {
                    classes.add(je.getName().replace(".class", "").replace("/", "."));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return classes;
    }

    /**
     * Load a list of .class files from a directory
     *
     * @param directory the base directory
     * @return list of class names
     */
    private static List<String> loadFromDir(File directory) {
        List<String> classes = new ArrayList<>();

        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                classes.add(DAO_PACKAGE + '.' + file.getName().substring(0, file.getName().length() - 6));
            }
        }

        return classes;
    }
}
