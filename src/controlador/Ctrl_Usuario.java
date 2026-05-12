/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import modelo.Usuario;
import conexion.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author JDJju
 */
public class Ctrl_Usuario {

    // Códigos de respuesta
    public static final int LOGIN_OK = 1;
    public static final int USUARIO_NO_EXISTE = 2;
    public static final int PASSWORD_INCORRECTA = 3;
    public static final int CUENTA_INACTIVA = 4;
    public static final int ERROR_CONEXION = 5;

    private Usuario usuarioLogueado = null;

    public int loginUser(String usuario, String password) {

        Connection cn = conexion.conectar();

        if (cn == null) return ERROR_CONEXION;

        try {
            String sql = "SELECT * FROM tb_usuario WHERE usuario = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close(); ps.close(); cn.close();
                return USUARIO_NO_EXISTE; // No existe el usuario
            }

            // El usuario existe, verificamos la contraseña
            String passDB = rs.getString("password");
            int estado = rs.getInt("estado");

            if (!passDB.equals(password)) {
                rs.close(); ps.close(); cn.close();
                return PASSWORD_INCORRECTA; // Contraseña incorrecta
            }

            // Contraseña correcta, verificamos si está activo
            if (estado == 0) {
                rs.close(); ps.close(); cn.close();
                return CUENTA_INACTIVA; // Cuenta desactivada
            }

            // Todo correcto, guardamos el objeto usuario
            usuarioLogueado = new Usuario();
            usuarioLogueado.setIdUsuario(rs.getInt("idUsuario"));
            usuarioLogueado.setNombre(rs.getString("nombre"));
            usuarioLogueado.setApellido(rs.getString("apellido"));
            usuarioLogueado.setUsuario(rs.getString("usuario"));
            usuarioLogueado.setPassword(rs.getString("password"));
            usuarioLogueado.setTelefono(rs.getString("telefono"));
            usuarioLogueado.setEstado(estado);
            usuarioLogueado.setRol(rs.getString("rol")); // ← Agregamos el rol

            rs.close(); ps.close(); cn.close();
            return LOGIN_OK;

        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_CONEXION;
        }
    }

    // Getter para obtener el usuario logueado
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Obtiene todos los usuarios activos con información completa para el panel admin
     */
    public List<Object[]> obtenerTodosActivos() {
        List<Object[]> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT idUsuario, nombre, apellido, usuario, telefono, rol, estado " +
                        "FROM tb_usuario WHERE estado = 1 ORDER BY nombre ASC";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idUsuario");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("apellido");
                fila[3] = rs.getString("usuario");
                fila[4] = rs.getString("telefono");
                fila[5] = rs.getString("rol");
                fila[6] = rs.getInt("estado") == 1 ? "Activo" : "Inactivo";
                lista.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener usuarios activos: " + e);
        }
        return lista;
    }

    /**
     * Obtiene todos los usuarios (activos e inactivos)
     */
    public List<Object[]> obtenerTodos() {
        List<Object[]> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT idUsuario, nombre, apellido, usuario, telefono, rol, estado " +
                        "FROM tb_usuario ORDER BY nombre ASC";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idUsuario");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("apellido");
                fila[3] = rs.getString("usuario");
                fila[4] = rs.getString("telefono");
                fila[5] = rs.getString("rol");
                fila[6] = rs.getInt("estado") == 1 ? "Activo" : "Inactivo";
                lista.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener todos los usuarios: " + e);
        }
        return lista;
    }

    /**
     * Obtiene el total de usuarios activos
     */
    public int getTotalUsuariosActivos() {
        int total = 0;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "SELECT COUNT(*) as total FROM tb_usuario WHERE estado = 1"
            );
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getTotalUsuariosActivos: " + e);
        }
        return total;
    }

    public List<Object[]> buscarUsuarios(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT idUsuario, nombre, apellido, usuario, telefono, rol, estado " +
                         "FROM tb_usuario WHERE " +
                         "(nombre LIKE ? OR apellido LIKE ? OR usuario LIKE ? OR rol LIKE ?) " +
                         "ORDER BY nombre ASC";
            PreparedStatement ps = cn.prepareStatement(sql);
            String valor = "%" + (filtro != null ? filtro.trim() : "") + "%";
            ps.setString(1, valor);
            ps.setString(2, valor);
            ps.setString(3, valor);
            ps.setString(4, valor);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idUsuario");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("apellido");
                fila[3] = rs.getString("usuario");
                fila[4] = rs.getString("telefono");
                fila[5] = rs.getString("rol");
                fila[6] = rs.getInt("estado") == 1 ? "Activo" : "Inactivo";
                lista.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al buscar usuarios: " + e);
        }
        return lista;
    }
}
