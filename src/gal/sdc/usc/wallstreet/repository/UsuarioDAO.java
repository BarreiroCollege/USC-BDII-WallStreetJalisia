package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsuarioDAO extends DAO<Usuario> {

    public UsuarioDAO(Connection conexion) {
        super(conexion, Usuario.class);
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM usuario"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Usuario.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Devuelve una lista con los usuarios que poseen más saldo (no bloqueado) en el mercado.
     * El número de usuarios se regula con el parámetro límite.
     * No se incluye al regulador en el resultado.
     *
     * @param limite       Número de usuarios máximo de la lista.
     * @param reguladorDAO DAO del regulador (permite eliminarlo del resultado)
     * @return Lista con los usuarios de mayor saldo con tamaño máximo limite.
     */
    public List<Usuario> getUsuariosMasSaldo(int limite, ReguladorDAO reguladorDAO) {
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario " +
                        "WHERE alta is null AND identificador != ? " +
                        "ORDER BY saldo DESC " +
                        "LIMIT ?"
        )) {
            ps.setString(1, reguladorDAO.getDatoRegulador("identificador"));
            ps.setInt(2, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Usuario.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /***
     * Devuelve una lista de usuarios filtrando por diferentes condiciones (id, empresa/inversor, DNI, nombre, apellidos,
     * CIF, nombre comercial).
     *
     * @param filtro HashMap donde se indican qué filtros se deben usar y con qué valores.
     * @param limite Máximo tamaño de la lista de usuarios a devolver.
     * @return Lista de hasta limite usuarios que cumplen las condiciones especificadas.
     */
    public List<Usuario> getUsuariosFiltroPersonalizado(HashMap<String, String> filtro, int limite) {
        List<Usuario> usuarios = new ArrayList<>();

        /*
         * Para simplificar la estructura de la query, se utiliza una view. Esta une las tablas de empresas e inversores
         * con la tabla de usuarios. Los atributos de invesores quedan nulos para las empresas, y viceversa.
         * Además, no se muestra ni a los usuarios inactivos ni al regulador.
         */

        String sql = "SELECT * " +
                "FROM empresas_inversores_usuarios";

        // Se construye el where de la consulta en función de los filtros indicados
        if (!filtro.isEmpty()) sql += construirWherePersonalizado(filtro);
        sql += " ORDER BY saldo DESC LIMIT ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            llenarPreparedStatement(ps, filtro, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(new Usuario.Builder().withSuperUsuario(
                        new SuperUsuario.Builder().withIdentificador(rs.getString("identificador")).build()
                        ).withSaldo(rs.getFloat("saldo")).build()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    // Construye el where de la consulta.
    private String construirWherePersonalizado(HashMap<String, String> filtro) {
        boolean primeroEncontrado = false;
        String sql = " WHERE";

        if (filtro.containsKey("id")) {
            sql += " identificador LIKE LOWER(?)";
            primeroEncontrado = true;
        }

        if ("Inversores".equals(filtro.get("tipo"))) {
            if (primeroEncontrado) {
                sql += " AND";
            }
            sql += " usuario_inversor is not null";

            if (filtro.containsKey("DNI")) {
                sql += " AND LOWER(dni) LIKE LOWER(?)";
            }

            if (filtro.containsKey("nombre")) {
                sql += " AND LOWER(nombre) LIKE LOWER(?)";
            }

            if (filtro.containsKey("apellidos")) {
                sql += " AND LOWER(apellidos) LIKE LOWER(?)";
            }

        } else if ("Empresas".equals(filtro.get("tipo"))) {
            if (primeroEncontrado) {
                sql += " AND";
            }
            sql += " usuario_empresa is not null";

            if (filtro.containsKey("CIF")) {
                sql += " AND LOWER(cif) LIKE LOWER(?)";
            }

            if (filtro.containsKey("nombre")) {
                sql += " AND LOWER(nombre_comercial) LIKE LOWER(?)";
            }
        }

        return sql;
    }

    // Rellena los campos de la consulta en el PreparedStatement.
    private void llenarPreparedStatement(PreparedStatement ps, HashMap<String, String> filtro, int limite) throws SQLException {
        int indice = 1;
        if (filtro.containsKey("id")) {
            ps.setString(indice, "%" + filtro.get("id") + "%");
            indice++;
        }
        if (filtro.containsKey("DNI")) {
            ps.setString(indice, "%" + filtro.get("DNI") + "%");
            indice++;
        } else if (filtro.containsKey("CIF")) {
            ps.setString(indice, "%" + filtro.get("CIF") + "%");
            indice++;
        }
        if (filtro.containsKey("nombre")) {
            ps.setString(indice, "%" + filtro.get("nombre") + "%");
            indice++;
        }
        if (filtro.containsKey("apellidos")) {
            ps.setString(indice, "%" + filtro.get("apellidos") + "%");
            indice++;
        }
        ps.setInt(indice, limite);
    }

    public List<Usuario> getUsuariosParticipacionEmpresa(String empresa) {
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * FROM usuario u, participacion p WHERE u.identificador = p.usuario AND p.empresa = ?"
        )) {
            ps.setString(1, empresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Usuario.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public Usuario getUsuario(String identificador) {
        return getUsuarios().stream().filter(user -> identificador.equals(user.getSuperUsuario().getIdentificador())).findFirst().orElse(null);

    }

    public List<UsuarioSesion> getUsuariosPorSociedad(Sociedad s) {
        List<UsuarioSesion> usuarios = new ArrayList<>();

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT i.* FROM inversor i, usuario u WHERE i.usuario = u.identificador AND u.sociedad = ?"
        )) {
            ps.setString(1, s.getSuperUsuario().getIdentificador());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Inversor.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT e.* FROM empresa e, usuario u WHERE e.usuario = u.identificador AND u.sociedad = ?"
        )) {
            ps.setString(1, s.getSuperUsuario().getIdentificador());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(Mapeador.map(rs, Empresa.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return usuarios;
    }

    /***
     * Devuelve una lista de usuarios que aún no están activos, pero lo han solicitado
     *
     * @return Lista de usuarios inactivos; null en caso de error
     */
    public List<Usuario> getInactivos() {
        List<Usuario> inactivos = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario " +
                        "WHERE alta is not null AND baja is null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                inactivos.add(Mapeador.map(rs, Usuario.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return inactivos;
    }

    /***
     * Devuelve una lista de usuarios que han solicitado darse de baja
     *
     * @return Lista de usuarios que pidieron baja; null en caso de error
     */
    public List<Usuario> getPendientesBaja() {
        List<Usuario> inactivos = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario " +
                        "WHERE alta is null AND baja is not null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                inactivos.add(Mapeador.map(rs, Usuario.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return inactivos;
    }

    /***
     * Acepta un usuario en la aplicación (cambia el campo activo a true)
     *
     * @param id Identificador del usuario a activar
     */
    public void aceptarUsuario(String id) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET alta = null " +
                        "WHERE identificador = ?"
        )) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * Rechaza un usuario de la aplicación (elimina sus datos)
     *
     * @param id Identificador del usuario del que se ha rechazado la solicitud
     */
    public void rechazarSolicitud(String id) {
        try (PreparedStatement psInversor = conexion.prepareStatement(
                "DELETE FROM inversor " +
                        "WHERE usuario = ?"
        )) {
            psInversor.setString(1, id);
            psInversor.executeUpdate();

            try (PreparedStatement psEmpresa = conexion.prepareStatement(
                    "DELETE FROM empresa " +
                            "WHERE usuario = ?"
            )) {
                psEmpresa.setString(1, id);
                psEmpresa.executeUpdate();
            }

            try (PreparedStatement psUsuario = conexion.prepareStatement(
                    "DELETE FROM usuario " +
                            "WHERE identificador = ?"
            )) {
                psUsuario.setString(1, id);
                psUsuario.executeUpdate();
            }

            try (PreparedStatement psSuperusuario = conexion.prepareStatement(
                    "DELETE FROM superusuario " +
                            "WHERE identificador = ?"
            )) {
                psSuperusuario.setString(1, id);
                psSuperusuario.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * Se da de alta a los usuarios de la lista indicada.
     *
     * @param usuariosPendientes Lista de usuarios a dar de alta.
     */
    public void aceptarUsuariosTodos(List<Usuario> usuariosPendientes) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET alta = null " +
                        "WHERE identificador = ? "
        )) {
            for (Usuario usuario : usuariosPendientes) {
                ps.setString(1, usuario.getSuperUsuario().getIdentificador());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se rechaza la baja de un superusuario (su cuenta no sufre mayores cambios).
     *
     * @param id Identificador del superusuario.
     * @return True, si no ha habido fallos; false, en caso contrario
     */
    public boolean rechazarBaja(String id) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET baja = null " +
                        "WHERE identificador = ?"
        )) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean retirarSaldo(float cantidad, Usuario usuario) {
        return retirarSaldo(cantidad, usuario.getSuperUsuario().getIdentificador());
    }

    public boolean retirarSaldo(float cantidad, String id) {
        return depositarSaldo(-cantidad, id);
    }

    public boolean depositarSaldo(float cantidad, Usuario usuario) {
        return depositarSaldo(cantidad, usuario.getSuperUsuario().getIdentificador());
    }

    /***
     * Transfiere una cierta cantidad al saldo de un usuario.
     *
     * @param cantidad Fondos a añadir
     * @param id Identificador del usuario.
     * @return true, si se ha realizado correctamente; false, en caso contrario.
     */
    public boolean depositarSaldo(float cantidad, String id) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET saldo = saldo + ? " +
                        "WHERE identificador = ?"
        )) {
            ps.setFloat(1, cantidad);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void vaciarSaldo(Usuario usuario) {
        vaciarSaldo(usuario.getSuperUsuario().getIdentificador());
    }

    /***
     * Retira todos los fondos de la cuenta de un usuario.
     *
     * @param id Identificador del usuario.
     */
    public void vaciarSaldo(String id) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET saldo = ?, saldo_bloqueado = ? " +
                        "WHERE identificador = ?"
        )) {
            ps.setFloat(1, 0);
            ps.setFloat(2, 0);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void darDeBajaUsuario(Usuario usuario) {
        darDeBajaUsuario(usuario.getSuperUsuario().getIdentificador());
    }

    /***
     * Se da de baja un usuario <-> tanto alta como baja quedan no nulos (ver UsuarioEstado)
     *
     * @param id Identificador del usuario a dar de baja
     */
    public void darDeBajaUsuario(String id) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET alta = now() " +
                        "WHERE identificador = ?"
        )) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * Da de baja a una lista de usuarios (para cada uno de ellos, alta y baja quedan no nulos -> ver UsuarioEstado)
     *
     * @param identificadores Lista de identificadores de los usuarios a dar de baja.
     */
    public void darDeBajaUsuarios(List<String> identificadores) {
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET alta = now() " +
                        "WHERE identificador = ?"
        )) {
            for (String identificador : identificadores) {
                ps.setString(1, identificador);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
