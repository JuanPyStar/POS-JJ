package controlador;

import conexion.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Factura;

public class Ctrl_Dashboard {

    public int getTotalVentasDia() {
        int total = 0;
        Connection cn = conexion.conectar();
        try {
            // Asumiendo que fechaFactura es DATETIME
            PreparedStatement consulta = cn.prepareStatement(
                "SELECT COUNT(*) as total FROM tb_factura WHERE DATE(fechaFactura) = CURDATE() AND estado = 1"
            );
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getTotalVentasDia: " + e);
        }
        return total;
    }

    public double getTotalIngresosDia() {
        double total = 0.0;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "SELECT SUM(totalPagar) as ingresos FROM tb_factura WHERE DATE(fechaFactura) = CURDATE() AND estado = 1"
            );
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("ingresos");
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getTotalIngresosDia: " + e);
        }
        return total;
    }

    public int getTotalProductos() {
        int total = 0;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "SELECT COUNT(*) as total FROM tb_producto WHERE estado = 1"
            );
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getTotalProductos: " + e);
        }
        return total;
    }

    public int getTotalUsuarios() {
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
            System.out.println("Error en getTotalUsuarios: " + e);
        }
        return total;
    }

    /**
     * Obtiene todas las facturas del día actual
     */
    public List<Object[]> getFacturasDia() {
        List<Object[]> facturas = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT f.idFactura, f.numeroFactura, f.fechaFactura, f.totalPagar, p.metodoPago " +
                         "FROM tb_factura f " +
                         "LEFT JOIN tb_pago p ON f.idFactura = p.idFactura " +
                         "WHERE DATE(f.fechaFactura) = CURDATE() AND f.estado = 1 " +
                         "ORDER BY f.idFactura DESC";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("idFactura");
                fila[1] = rs.getString("numeroFactura");
                fila[2] = rs.getString("fechaFactura");
                fila[3] = rs.getDouble("totalPagar");
                fila[4] = rs.getString("metodoPago") != null ? rs.getString("metodoPago") : "N/A";
                facturas.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getFacturasDia: " + e);
        }
        return facturas;
    }

    /**
     * Obtiene los movimientos de inventario con detalles de producto
     */
    public List<Object[]> getMovimientosInventario() {
        return getMovimientosInventario(null);
    }

    public List<Object[]> getMovimientosInventario(String tipo) {
        List<Object[]> movimientos = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT m.idMovimiento, p.nombre, p.cantidad, m.tipoMovimiento, m.cantidad as cantMovimiento, " +
                "m.fechaMovimiento FROM tb_movimiento_inventario m " +
                "JOIN tb_producto p ON m.idProducto = p.idProducto "
            );
            if (tipo != null && !tipo.isEmpty()) {
                sql.append("WHERE m.tipoMovimiento = ? ");
            }
            sql.append("ORDER BY m.fechaMovimiento DESC LIMIT 100");

            PreparedStatement ps = cn.prepareStatement(sql.toString());
            if (tipo != null && !tipo.isEmpty()) {
                ps.setString(1, tipo);
            }
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idMovimiento");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getInt("cantidad");
                fila[3] = rs.getString("tipoMovimiento");
                fila[4] = rs.getInt("cantMovimiento");
                fila[5] = rs.getString("fechaMovimiento");
                fila[6] = rs.getString("tipoMovimiento").equals("ENTRADA") ? "Entrada" : "Salida";
                movimientos.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getMovimientosInventario: " + e);
        }
        return movimientos;
    }

    public boolean registrarMovimientoInventario(int idProducto, String tipoMovimiento, int cantidad) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        try {
            cn.setAutoCommit(false);

            if (cantidad <= 0 || (!"ENTRADA".equals(tipoMovimiento) && !"SALIDA".equals(tipoMovimiento))) {
                return false;
            }

            String updateStockSql = "UPDATE tb_producto SET cantidad = cantidad " + ("ENTRADA".equals(tipoMovimiento) ? "+ ?" : "- ?") + " WHERE idProducto = ?" + ("SALIDA".equals(tipoMovimiento) ? " AND cantidad >= ?" : "");
            PreparedStatement psStock = cn.prepareStatement(updateStockSql);
            psStock.setInt(1, cantidad);
            psStock.setInt(2, idProducto);
            if ("SALIDA".equals(tipoMovimiento)) {
                psStock.setInt(3, cantidad);
            }
            int actualizaciones = psStock.executeUpdate();

            if (actualizaciones == 0) {
                cn.rollback();
                return false;
            }

            PreparedStatement psMovimiento = cn.prepareStatement(
                "INSERT INTO tb_movimiento_inventario (idProducto, tipoMovimiento, cantidad, fechaMovimiento) VALUES (?, ?, ?, NOW())"
            );
            psMovimiento.setInt(1, idProducto);
            psMovimiento.setString(2, tipoMovimiento);
            psMovimiento.setInt(3, cantidad);
            psMovimiento.executeUpdate();

            cn.commit();
            respuesta = true;
        } catch (SQLException e) {
            System.out.println("Error al registrar movimiento de inventario: " + e);
            try {
                if (cn != null) cn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error en rollback: " + ex);
            }
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e);
            }
        }
        return respuesta;
    }

    /**
     * Obtiene reportes de ventas por fecha
     */
    public List<Object[]> getReporteVentas(String tipo) {
        List<Object[]> reportes = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT f.idFactura, f.numeroFactura, f.fechaFactura, f.totalPagar, " +
                        "c.nombre, u.nombre as vendedor, p.metodoPago " +
                        "FROM tb_factura f " +
                        "LEFT JOIN tb_cliente c ON f.idCliente = c.idCliente " +
                        "LEFT JOIN tb_usuario u ON f.idUsuario = u.idUsuario " +
                        "LEFT JOIN tb_pago p ON f.idFactura = p.idFactura " +
                        "WHERE f.estado = 1 ";
            
            if ("diario".equals(tipo)) {
                sql += "AND DATE(f.fechaFactura) = CURDATE() ";
            } else if ("mensual".equals(tipo)) {
                sql += "AND MONTH(f.fechaFactura) = MONTH(NOW()) AND YEAR(f.fechaFactura) = YEAR(NOW()) ";
            }
            
            sql += "ORDER BY f.fechaFactura DESC";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idFactura");
                fila[1] = rs.getString("numeroFactura");
                fila[2] = rs.getString("fechaFactura");
                fila[3] = "$ " + String.format("%.2f", rs.getDouble("totalPagar"));
                fila[4] = rs.getString("nombre") != null ? rs.getString("nombre") : "Consumidor Final";
                fila[5] = rs.getString("vendedor");
                fila[6] = rs.getString("metodoPago") != null ? rs.getString("metodoPago") : "N/A";
                reportes.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getReporteVentas: " + e);
        }
        return reportes;
    }

    public List<Object[]> getVentasSemanales() {
        List<Object[]> ventas = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT DATE(f.fechaFactura) as fecha, SUM(f.totalPagar) as total " +
                         "FROM tb_factura f " +
                         "WHERE f.estado = 1 AND DATE(f.fechaFactura) >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                         "GROUP BY DATE(f.fechaFactura) ORDER BY fecha ASC";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Map<LocalDate, Double> totalesPorDia = new HashMap<>();
            while (rs.next()) {
                Date fecha = rs.getDate("fecha");
                if (fecha != null) {
                    totalesPorDia.put(fecha.toLocalDate(), rs.getDouble("total"));
                }
            }
            cn.close();

            LocalDate hoy = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
            for (int i = 6; i >= 0; i--) {
                LocalDate dia = hoy.minusDays(i);
                double total = totalesPorDia.getOrDefault(dia, 0.0);
                ventas.add(new Object[]{dia.format(formatter), total});
            }
        } catch (SQLException e) {
            System.out.println("Error en getVentasSemanales: " + e);
        }
        return ventas;
    }

    /**
     * Obtiene los productos más vendidos
     */
    public List<Object[]> getProductosMasVendidos() {
        List<Object[]> productos = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT p.idProducto, p.nombre, SUM(d.cantidad) as totalVendido, " +
                        "SUM(d.total) as ingresoTotal FROM tb_detalle_factura d " +
                        "JOIN tb_producto p ON d.idProducto = p.idProducto " +
                        "JOIN tb_factura f ON d.idFactura = f.idFactura " +
                        "WHERE f.estado = 1 GROUP BY p.idProducto, p.nombre " +
                        "ORDER BY totalVendido DESC LIMIT 10";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getInt("idProducto");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getInt("totalVendido");
                fila[3] = "$ " + String.format("%.2f", rs.getDouble("ingresoTotal"));
                productos.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getProductosMasVendidos: " + e);
        }
        return productos;
    }
}
