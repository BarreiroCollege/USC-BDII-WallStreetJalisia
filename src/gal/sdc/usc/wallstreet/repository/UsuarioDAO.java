package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.*;
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
     *
     * @param limite Número de usuarios máximo de la lista.
     * @return Lista con los usuarios de mayor saldo con tamaño máximo limite.
     */
    public List<Usuario> getUsuariosMasSaldo(int limite) {
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario " +
                        "WHERE alta is null " +
                        "ORDER BY saldo " +
                        "LIMIT ?"
        )) {
            ps.setInt(1, limite);
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
    public List<Usuario> getUsuariosFiltroPersonalizado(HashMap<String, String> filtro, int limite){
        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT * " +
                "FROM (empresa e RIGHT JOIN usuario u ON e.usuario = u.identificador) as m LEFT JOIN inversor i ON m.identificador = i.usuario";

        int longitud = filtro.size();
        if (longitud == 1){
            sql += construirWherePersonalizado1(filtro);
        } else if (longitud > 1){
            sql += construirWherePersonalizadoMultiple(filtro);
        }
        sql += " LIMIT ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            if (!filtro.isEmpty()){
                llenarPreparedStatement(ps, filtro, limite);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    usuarios.add(new Usuario.Builder().withSuperUsuario(
                            new SuperUsuario.Builder().withIdentificador(rs.getString("identificador")).build()
                            ).withSaldo(rs.getFloat("saldo")).build()
                    );
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return usuarios;
    }

    // Construye el where de la consulta cuando solo hay una condición.
    private String construirWherePersonalizado1(HashMap<String, String> filtro) {
        String sql = " WHERE";
        if (filtro.containsKey("id")){
            sql += " m.identificador = ?";
            return sql;
        }
        if ("Inversores".equals(filtro.get("tipo"))) {
            sql += " i.usuario is not null";
            return sql;
        }
        if (filtro.containsKey("dni")) {
            sql += " i.dni = ?";
            return sql;
        }
        if (filtro.containsKey("nombre")) {
            sql += " i.nombre = ?";
            return sql;
        }
        if (filtro.containsKey("apellidos")) {
            sql += " i.apellidos = ?";
            return sql;
        }
        if ("Empresas".equals(filtro.get("tipo"))) {
            sql += " m.usuario is not null";
            return sql;
        }
        if (filtro.containsKey("cif")) {
            sql += " m.cif = ?";
            return sql;
        }
        if (filtro.containsKey("nombreComercial")) {
            sql += " m.nombre = ?";
        }
        return sql;
    }

    // Construye el where de la consulta cunado hay más de una condición.
    private String construirWherePersonalizadoMultiple(HashMap<String, String> filtro){
        boolean primeroEncontrado = false;
        String sql = " WHERE";
        if (filtro.containsKey("id")){
            sql += " m.identificador = ?";
            primeroEncontrado = true;
        }
        if ("Inversores".equals(filtro.get("tipo"))) {
            if (primeroEncontrado) {
                sql += " AND";
            } else {
                primeroEncontrado = true;
            }
            sql += " i.usuario is not null";
            primeroEncontrado = true;
        } else if ("Empresas".equals(filtro.get("tipo"))) {
            if (primeroEncontrado) {
                sql += " AND";
            } else {
                primeroEncontrado = true;
            }
            sql += " m.usuario is not null";
        }
        if (filtro.containsKey("dni")) {
            if (primeroEncontrado) {
                sql += " AND";
            } else {
                primeroEncontrado = true;
            }
            sql += " i.dni = ?";
        }
        if (filtro.containsKey("nombre")) {
            if (primeroEncontrado) {
                sql += " AND";
            } else {
                primeroEncontrado = true;
            }
            sql += " i.nombre = ?";
        }
        if (filtro.containsKey("apellidos")) {
            if (primeroEncontrado) {
                sql += " AND";
            } else {
                primeroEncontrado = true;
            }
            sql += " i.apellidos = ?";
        }
        if (filtro.containsKey("cif")) {
            if (primeroEncontrado) {
                sql += " AND";
            } else {
                primeroEncontrado = true;
            }
            sql += " m.cif = ?";
        }
        if (filtro.containsKey("nombreComercial")) {
            if (primeroEncontrado) {
                sql += " AND";
            }
            sql += " m.nombre = ?";
        }
        return sql;
    }

    // Rellena los campos de la consulta en el PreparedStatement.
    private void llenarPreparedStatement(PreparedStatement ps, HashMap<String, String> filtro, int limite) throws SQLException {
        int indice = 1;
        if (filtro.containsKey("id")){
            ps.setString(indice, filtro.get("id"));
            indice++;
        }
        if (filtro.containsKey("DNI")){
            ps.setString(indice, filtro.get("DNI"));
            indice++;
        } else if (filtro.containsKey("CIF")){
            ps.setString(indice, filtro.get("CIF"));
            indice++;
        }
        if (filtro.containsKey("nombre")){
            ps.setString(indice, filtro.get("nombre"));
            indice++;
        }
        if (filtro.containsKey("apellidos")){
            ps.setString(indice, filtro.get("apellidos"));
            indice++;
        }
        ps.setInt(indice, limite);
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
     * @return Lista de usuarios inactivos
     */
    public List<Usuario> getInactivos(){
        List<Usuario> inactivos = new ArrayList<>();
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT * " +
                        "FROM usuario " +
                        "WHERE alta is not null"
        )) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                inactivos.add(Mapeador.map(rs, Usuario.class));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return inactivos;
    }

    /***
     * Devuelve el número de usuarios que han solicitado darse de baja
     *
     * @return Número de usuarios que quieren darse de baja; null en caso de error
     */
    public Integer getNumSolicitudesBaja(){
        Integer solicitudesBaja = null;
        try (PreparedStatement ps = super.conexion.prepareStatement(
                "SELECT count(*) as bajas " +
                        "FROM usuario " +
                        "WHERE baja is not null"
        )) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                solicitudesBaja = rs.getInt("bajas");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return solicitudesBaja;
    }

    /***
     * Acepta un usuario en la aplicación (cambia el campo activo a true)
     *
     * @param id Identificador del usuario a activar
     */
    public void aceptarUsuario(String id){
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
     * Se da de alta a los usuarios de la lista indicada.
     *
     * @param usuariosPendientes Lista de usuarios a dar de alta.
     */
    public void aceptarUsuariosTodos(List<Usuario> usuariosPendientes){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET alta = null " +
                        "WHERE identificador = ? "
        )) {
            for (Usuario usuario : usuariosPendientes) {
                ps.setString(1, usuario.getSuperUsuario().getIdentificador());
                ps.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Se rechaza la baja de un superusuario (su cuenta no sufre mayores cambios).
     *
     * @param id Identificador del superusuario.
     */
    public void rechazarBaja(String id){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET baja = null " +
                        "WHERE identificador = ?"
        )){
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean retirarSaldo(float cantidad, Usuario usuario){
        return retirarSaldo(cantidad, usuario.getSuperUsuario().getIdentificador());
    }

    public boolean retirarSaldo(float cantidad, String id){
        return depositarSaldo(-cantidad, id);
    }

    public boolean depositarSaldo(float cantidad, Usuario usuario){
        return depositarSaldo(cantidad, usuario.getSuperUsuario().getIdentificador());
    }

    /***
     * Transfiere una cierta cantidad al saldo de un usuario.
     *
     * @param cantidad Fondos a añadir
     * @param id Identificador del usuario.
     * @return true, si se ha realizado correctamente; false, en caso contrario.
     */
    public boolean depositarSaldo(float cantidad, String id){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET saldo = saldo + ? " +
                        "WHERE identificador = ?"
        )){
            ps.setFloat(1, cantidad);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void vaciarSaldo(Usuario usuario){
        vaciarSaldo(usuario.getSuperUsuario().getIdentificador());
    }

    /***
     * Retira todos los fondos de la cuenta de un usuario.
     *
     * @param id Identificador del usuario.
     */
    public void vaciarSaldo(String id){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET saldo = ? " +
                        "WHERE identificador = ?"
        )){
            ps.setFloat(1, 0);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void darDeBajaUsuario(Usuario usuario){
        darDeBajaUsuario(usuario.getSuperUsuario().getIdentificador());
    }

    /***
     * Se da de baja un usuario <-> tanto alta como baja quedan no nulos (ver UsuarioEstado)
     *
     * @param id Identificador del usuario a dar de baja
     */
    public void darDeBajaUsuario(String id){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET alta = now() " +
                        "WHERE identificador = ?"
        )){
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /***
     * Da de baja a una lista de usuarios (para cada uno de ellos, alta y baja quedan no nulos -> ver UsuarioEstado)
     *
     * @param identificadores Lista de identificadores de los usuarios a dar de baja.
     */
    public void darDeBajaUsuarios(List<String> identificadores){
        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario " +
                        "SET alta = now () " +
                        "WHERE identificador = ?"
        )){
            for (String identificador : identificadores){
                ps.setString(1, identificador);
                ps.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
