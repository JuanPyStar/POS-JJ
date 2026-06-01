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
        Ctrl_Turno ctrlTurno = new Ctrl_Turno();
        modelo.Turno turnoActivo = ctrlTurno.getTurnoActivo();
        try {
            String sql;
            if (turnoActivo != null) {
                sql = "SELECT COUNT(*) as total FROM tb_factura WHERE idTurno = " + turnoActivo.getIdTurno() + " AND estado = 1";
            } else {
                sql = "SELECT COUNT(*) as total FROM tb_factura WHERE DATE(fechaFactura) = CURDATE() AND estado = 1";
            }
            PreparedStatement consulta = cn.prepareStatement(sql);
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
        Ctrl_Turno ctrlTurno = new Ctrl_Turno();
        modelo.Turno turnoActivo = ctrlTurno.getTurnoActivo();
        try {
            String sql;
            if (turnoActivo != null) {
                sql = "SELECT SUM(totalPagar) as ingresos FROM tb_factura WHERE idTurno = " + turnoActivo.getIdTurno() + " AND estado = 1";
            } else {
                sql = "SELECT SUM(totalPagar) as ingresos FROM tb_factura WHERE DATE(fechaFactura) = CURDATE() AND estado = 1";
            }
            PreparedStatement consulta = cn.prepareStatement(sql);
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
        Ctrl_Turno ctrlTurno = new Ctrl_Turno();
        modelo.Turno turnoActivo = ctrlTurno.getTurnoActivo();
        try {
            String condicion;
            if (turnoActivo != null) {
                condicion = "f.idTurno = " + turnoActivo.getIdTurno() + " ";
            } else {
                condicion = "DATE(f.fechaFactura) = CURDATE() ";
            }
            String sql = "SELECT f.idFactura, f.numeroFactura, f.fechaFactura, f.totalPagar, p.metodoPago " +
                         "FROM tb_factura f " +
                         "LEFT JOIN tb_pago p ON f.idFactura = p.idFactura " +
                         "WHERE " + condicion + "AND f.estado = 1 " +
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
        return getMovimientosInventario(null, null, null);
    }

    public List<Object[]> getMovimientosInventario(String tipo, String busquedaProducto, String fecha) {
        List<Object[]> movimientos = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT m.idMovimiento, p.nombre, p.cantidad, m.tipoMovimiento, m.cantidad as cantMovimiento, " +
                "m.fechaMovimiento FROM tb_movimiento_inventario m " +
                "JOIN tb_producto p ON m.idProducto = p.idProducto WHERE 1=1 "
            );
            if (tipo != null && !tipo.isEmpty()) {
                sql.append("AND m.tipoMovimiento = ? ");
            }
            if (busquedaProducto != null && !busquedaProducto.trim().isEmpty()) {
                sql.append("AND p.nombre LIKE ? ");
            }
            if (fecha != null && !fecha.isEmpty() && !fecha.equals("Todas las fechas")) {
                sql.append("AND DATE(m.fechaMovimiento) = ? ");
            }
            sql.append("ORDER BY m.fechaMovimiento DESC LIMIT 100");

            PreparedStatement ps = cn.prepareStatement(sql.toString());
            int index = 1;
            if (tipo != null && !tipo.isEmpty()) {
                ps.setString(index++, tipo);
            }
            if (busquedaProducto != null && !busquedaProducto.trim().isEmpty()) {
                ps.setString(index++, "%" + busquedaProducto.trim() + "%");
            }
            if (fecha != null && !fecha.isEmpty() && !fecha.equals("Todas las fechas")) {
                ps.setString(index++, fecha);
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
                if (cn != null) {
                    cn.setAutoCommit(true);
                    cn.close();
                }
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
                Ctrl_Turno ctrlTurno = new Ctrl_Turno();
                modelo.Turno turnoActivo = ctrlTurno.getTurnoActivo();
                if (turnoActivo != null) {
                    sql += "AND f.idTurno = " + turnoActivo.getIdTurno() + " ";
                } else {
                    sql += "AND DATE(f.fechaFactura) = CURDATE() ";
                }
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
                fila[3] = rs.getString("nombre") != null ? rs.getString("nombre") : "Consumidor Final"; // CLIENTE
                fila[4] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", rs.getDouble("totalPagar")); // TOTAL
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
            // Mostrar los últimos 7 turnos en la gráfica
            String sql = "SELECT t.idTurno, t.fechaApertura, " +
                         "(SELECT COALESCE(SUM(totalPagar), 0) FROM tb_factura WHERE idTurno = t.idTurno AND estado = 1) as totalVentasCalculado " +
                         "FROM tb_turno t ORDER BY t.idTurno DESC LIMIT 7";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Object[]> turnosList = new ArrayList<>();
            while (rs.next()) {
                String fechaCompleta = rs.getString("fechaApertura");
                // Extraer 'dd/MM HH:mm'
                String etiqueta = "T" + rs.getInt("idTurno") + " " + fechaCompleta.substring(8, 10) + "/" + fechaCompleta.substring(5, 7);
                double total = rs.getDouble("totalVentasCalculado");
                turnosList.add(new Object[]{etiqueta, total});
            }
            cn.close();

            // Invertir para que el más antiguo salga a la izquierda y el más nuevo a la derecha
            for (int i = turnosList.size() - 1; i >= 0; i--) {
                ventas.add(turnosList.get(i));
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
                fila[3] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", rs.getDouble("ingresoTotal"));
                productos.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getProductosMasVendidos: " + e);
        }
        return productos;
    }

    public List<Object[]> getReporteVentasMetodoPago() {
        List<Object[]> reporte = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT COALESCE(p.metodoPago, 'No especificado') as metodo, " +
                         "COUNT(f.idFactura) as totalOperaciones, " +
                         "SUM(f.totalPagar) as ingresoTotal " +
                         "FROM tb_factura f " +
                         "LEFT JOIN tb_pago p ON f.idFactura = p.idFactura " +
                         "WHERE f.estado = 1 " +
                         "GROUP BY p.metodoPago " +
                         "ORDER BY ingresoTotal DESC";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getString("metodo");
                fila[1] = rs.getInt("totalOperaciones");
                fila[2] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", rs.getDouble("ingresoTotal"));
                reporte.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getReporteVentasMetodoPago: " + e);
        }
        return reporte;
    }

    public List<Object[]> getReporteRendimientoCajeros() {
        List<Object[]> reporte = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT u.nombre as vendedor, " +
                         "COUNT(f.idFactura) as facturasEmitidas, " +
                         "SUM(f.totalPagar) as totalVendido " +
                         "FROM tb_factura f " +
                         "JOIN tb_usuario u ON f.idUsuario = u.idUsuario " +
                         "WHERE f.estado = 1 " +
                         "GROUP BY u.idUsuario, u.nombre " +
                         "ORDER BY totalVendido DESC";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getString("vendedor");
                fila[1] = rs.getInt("facturasEmitidas");
                fila[2] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", rs.getDouble("totalVendido"));
                reporte.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getReporteRendimientoCajeros: " + e);
        }
        return reporte;
    }

    public List<Object[]> getReporteStockCritico() {
        List<Object[]> reporte = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            // Umbral de stock crítico: <= 10
            String sql = "SELECT p.idProducto, p.nombre, p.cantidad, c.descripcion as categoria " +
                         "FROM tb_producto p " +
                         "JOIN tb_categoria c ON p.idCategoria = c.idCategoria " +
                         "WHERE p.estado = 1 AND p.cantidad <= 10 " +
                         "ORDER BY p.cantidad ASC";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getInt("idProducto");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getInt("cantidad");
                fila[3] = rs.getString("categoria");
                reporte.add(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error en getReporteStockCritico: " + e);
        }
        return reporte;
    }
}
