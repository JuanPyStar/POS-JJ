/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import conexion.conexion;
import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // 🟢 Crear usuario
    public boolean crearUsuario(Usuario u) {
        String sql = "INSERT INTO usuario (nombre, apellido, usuario, password, telefono, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            con = conexion.conectar();
            ps = con.prepareStatement(sql);

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getTelefono());
            ps.setInt(6, u.getEstado());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    // 🔵 Consultar todos
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try {
            con = conexion.conectar();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();

                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setTelefono(rs.getString("telefono"));
                u.setEstado(rs.getInt("estado"));

                lista.add(u);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }

        return lista;
    }

    // 🔍 Buscar por ID
    public Usuario buscarUsuario(int id) {
        String sql = "SELECT * FROM usuario WHERE idUsuario = ?";
        Usuario u = new Usuario();

        try {
            con = conexion.conectar();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setTelefono(rs.getString("telefono"));
                u.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }

        return u;
    }

    // 🟡 Actualizar
    public boolean actualizarUsuario(Usuario u) {
        String sql = "UPDATE usuario SET nombre=?, apellido=?, usuario=?, password=?, telefono=?, estado=? WHERE idUsuario=?";

        try {
            con = conexion.conectar();
            ps = con.prepareStatement(sql);

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getTelefono());
            ps.setInt(6, u.getEstado());
            ps.setInt(7, u.getIdUsuario());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // 🔴 Eliminar
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuario WHERE idUsuario=?";

        try {
            con =conexion.conectar();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    
    
    
    
}
