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
            // Primero verificamos si el usuario existe
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
}
