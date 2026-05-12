package controlador;

import conexion.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Producto;

public class Ctrl_Producto {

    // Método para guardar un nuevo producto
    public boolean guardar(Producto objeto) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "insert into tb_producto (nombre, cantidad, precio, descripcion, porcentajeIva, idCategoria, estado) values(?,?,?,?,?,?,?)"
            );
            consulta.setString(1, objeto.getNombre());
            consulta.setInt(2, objeto.getCantidad());
            consulta.setDouble(3, objeto.getPrecio());
            consulta.setString(4, objeto.getDescripcion());
            consulta.setInt(5, objeto.getPorcentajeIva());
            consulta.setInt(6, objeto.getIdCategoria());
            consulta.setInt(7, objeto.getEstado());

            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al guardar producto: " + e);
        }
        return respuesta;
    }

    // Método para listar productos
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement("select * from tb_producto where estado = 1");
            ResultSet rs = consulta.executeQuery();

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setCantidad(rs.getInt("cantidad"));
                p.setPrecio(rs.getDouble("precio"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPorcentajeIva(rs.getInt("porcentajeIva"));
                p.setIdCategoria(rs.getInt("idCategoria"));
                p.setEstado(rs.getInt("estado"));
                
                lista.add(p);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener productos: " + e);
        }
        return lista;
    }

    // Método para buscar productos por nombre (para el Punto de Venta)
    public List<Producto> buscarProductos(String filtro) {
        List<Producto> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement("select * from tb_producto where estado = 1 and nombre like ?");
            consulta.setString(1, "%" + filtro + "%");
            ResultSet rs = consulta.executeQuery();

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setCantidad(rs.getInt("cantidad"));
                p.setPrecio(rs.getDouble("precio"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPorcentajeIva(rs.getInt("porcentajeIva"));
                p.setIdCategoria(rs.getInt("idCategoria"));
                p.setEstado(rs.getInt("estado"));
                
                lista.add(p);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al buscar productos: " + e);
        }
        return lista;
    }
}
