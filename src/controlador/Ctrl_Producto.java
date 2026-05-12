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

    public Producto obtenerPorId(int idProducto) {
        Producto producto = null;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement("select * from tb_producto where idProducto = ?");
            consulta.setInt(1, idProducto);
            ResultSet rs = consulta.executeQuery();

            if (rs.next()) {
                producto = new Producto();
                producto.setIdProducto(rs.getInt("idProducto"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPorcentajeIva(rs.getInt("porcentajeIva"));
                producto.setIdCategoria(rs.getInt("idCategoria"));
                producto.setEstado(rs.getInt("estado"));
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener producto por ID: " + e);
        }
        return producto;
    }

    public boolean actualizar(Producto objeto) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "UPDATE tb_producto SET nombre = ?, cantidad = ?, precio = ?, descripcion = ?, porcentajeIva = ?, idCategoria = ?, estado = ? WHERE idProducto = ?"
            );
            consulta.setString(1, objeto.getNombre());
            consulta.setInt(2, objeto.getCantidad());
            consulta.setDouble(3, objeto.getPrecio());
            consulta.setString(4, objeto.getDescripcion());
            consulta.setInt(5, objeto.getPorcentajeIva());
            consulta.setInt(6, objeto.getIdCategoria());
            consulta.setInt(7, objeto.getEstado());
            consulta.setInt(8, objeto.getIdProducto());

            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e);
        }
        return respuesta;
    }

    public boolean eliminar(int idProducto) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "UPDATE tb_producto SET estado = 0 WHERE idProducto = ?"
            );
            consulta.setInt(1, idProducto);
            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e);
        }
        return respuesta;
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

    /**
     * Obtiene todos los productos con información de categoría para el panel admin
     */
    public List<Object[]> obtenerTodosConCategoria() {
        List<Object[]> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT p.idProducto, p.nombre, p.cantidad, p.precio, c.descripcion as categoria, " +
                        "p.porcentajeIva, p.estado FROM tb_producto p " +
                        "LEFT JOIN tb_categoria c ON p.idCategoria = c.idCategoria " +
                        "WHERE p.estado = 1 ORDER BY p.nombre ASC";
            PreparedStatement consulta = cn.prepareStatement(sql);
            ResultSet rs = consulta.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idProducto");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getInt("cantidad");
                fila[3] = "$ " + String.format("%.2f", rs.getDouble("precio"));
                fila[4] = rs.getString("categoria");
                fila[5] = rs.getInt("porcentajeIva") + "%";
                fila[6] = rs.getInt("estado") == 1 ? "Activo" : "Inactivo";
                lista.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener productos con categoría: " + e);
        }
        return lista;
    }

    /**
     * Obtiene productos con stock bajo
     */
    public List<Object[]> obtenerStockBajo() {
        List<Object[]> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT p.idProducto, p.nombre, p.cantidad, p.precio, c.descripcion as categoria " +
                        "FROM tb_producto p LEFT JOIN tb_categoria c ON p.idCategoria = c.idCategoria " +
                        "WHERE p.cantidad < 10 AND p.estado = 1 ORDER BY p.cantidad ASC";
            PreparedStatement consulta = cn.prepareStatement(sql);
            ResultSet rs = consulta.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("idProducto");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getInt("cantidad");
                fila[3] = "$ " + String.format("%.2f", rs.getDouble("precio"));
                fila[4] = rs.getString("categoria");
                lista.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener stock bajo: " + e);
        }
        return lista;
    }

    public List<Object[]> buscarProductos(String filtro, int idCategoria) {
        List<Object[]> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT p.idProducto, p.nombre, p.cantidad, p.precio, c.descripcion as categoria, " +
                "p.porcentajeIva, p.estado FROM tb_producto p " +
                "LEFT JOIN tb_categoria c ON p.idCategoria = c.idCategoria " +
                "WHERE p.estado = 1"
            );

            if (filtro != null && !filtro.trim().isEmpty()) {
                sql.append(" AND (p.nombre LIKE ? OR p.descripcion LIKE ? OR c.descripcion LIKE ?)");
            }
            if (idCategoria > 0) {
                sql.append(" AND p.idCategoria = ?");
            }
            sql.append(" ORDER BY p.nombre ASC");

            PreparedStatement consulta = cn.prepareStatement(sql.toString());
            int idx = 1;
            if (filtro != null && !filtro.trim().isEmpty()) {
                String valor = "%" + filtro.trim() + "%";
                consulta.setString(idx++, valor);
                consulta.setString(idx++, valor);
                consulta.setString(idx++, valor);
            }
            if (idCategoria > 0) {
                consulta.setInt(idx++, idCategoria);
            }

            ResultSet rs = consulta.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idProducto");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getInt("cantidad");
                fila[3] = "$ " + String.format("%.2f", rs.getDouble("precio"));
                fila[4] = rs.getString("categoria");
                fila[5] = rs.getInt("porcentajeIva") + "%";
                fila[6] = rs.getInt("estado") == 1 ? "Activo" : "Inactivo";
                lista.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al buscar productos: " + e);
        }
        return lista;
    }

    /**
     * Obtiene el conteo de categorías
     */
    public int getTotalCategorias() {
        int total = 0;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "SELECT COUNT(*) as total FROM tb_categoria WHERE estado = 1"
            );
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getTotalCategorias: " + e);
        }
        return total;
    }
}
