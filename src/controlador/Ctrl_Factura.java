package controlador;

import conexion.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import modelo.DetalleFactura;
import modelo.Factura;

public class Ctrl_Factura {

    public boolean guardarVenta(Factura factura, List<DetalleFactura> detalles, String metodoPago) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        
        try {
            // Iniciar transacción
            cn.setAutoCommit(false);
            
            // 0. Asegurar que exista un cliente por defecto (Consumidor Final) para evitar error de Llave Foránea
            PreparedStatement psCheckCliente = cn.prepareStatement("SELECT idCliente FROM tb_cliente WHERE idCliente = 1");
            ResultSet rsCliente = psCheckCliente.executeQuery();
            if (!rsCliente.next()) {
                // Si no existe, lo creamos
                PreparedStatement psInsertCliente = cn.prepareStatement(
                    "INSERT INTO tb_cliente (idCliente, nombre, apellido, cedula, telefono, direccion, estado) " +
                    "VALUES (1, 'Consumidor', 'Final', '0000000000', '0000000000', 'S/N', 1)"
                );
                psInsertCliente.executeUpdate();
            }
            
            // 1. Guardar la Factura
            PreparedStatement psFactura = cn.prepareStatement(
                "INSERT INTO tb_factura (numeroFactura, idCliente, idUsuario, subtotal, totalIva, totalPagar, fechaFactura, estado) " +
                "VALUES (?, 1, ?, ?, ?, ?, NOW(), ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            
            psFactura.setString(1, factura.getNumeroFactura());
            psFactura.setInt(2, factura.getIdUsuario());
            psFactura.setDouble(3, factura.getSubtotal());
            psFactura.setDouble(4, factura.getTotalIva());
            psFactura.setDouble(5, factura.getTotalPagar());
            psFactura.setInt(6, 1); // Estado activo
            
            psFactura.executeUpdate();
            
            // Obtener el ID de la factura generada
            ResultSet rs = psFactura.getGeneratedKeys();
            int idFacturaGenerada = 0;
            if (rs.next()) {
                idFacturaGenerada = rs.getInt(1);
            }
            
            if (idFacturaGenerada > 0) {
                // 2. Guardar el Pago en tb_pago
                PreparedStatement psPago = cn.prepareStatement(
                    "INSERT INTO tb_pago (idFactura, metodoPago, valorPagado, fechaPago) VALUES (?, ?, ?, NOW())"
                );
                psPago.setInt(1, idFacturaGenerada);
                psPago.setString(2, metodoPago);
                psPago.setDouble(3, factura.getTotalPagar());
                psPago.executeUpdate();

                // Preparar sentencias para el ciclo
                PreparedStatement psDetalle = cn.prepareStatement(
                    "INSERT INTO tb_detalle_factura (idFactura, idProducto, cantidad, precioUnitario, subtotal, descuento, iva, total, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                
                PreparedStatement psStock = cn.prepareStatement(
                    "UPDATE tb_producto SET cantidad = cantidad - ? WHERE idProducto = ?"
                );

                PreparedStatement psMovimiento = cn.prepareStatement(
                    "INSERT INTO tb_movimiento_inventario (idProducto, tipoMovimiento, cantidad, fechaMovimiento) " +
                    "VALUES (?, 'SALIDA', ?, NOW())"
                );
                
                for (DetalleFactura d : detalles) {
                    // Detalle
                    psDetalle.setInt(1, idFacturaGenerada);
                    psDetalle.setInt(2, d.getIdProducto());
                    psDetalle.setInt(3, d.getCantidad());
                    psDetalle.setDouble(4, d.getPrecioUnitario());
                    psDetalle.setDouble(5, d.getSubtotal());
                    psDetalle.setDouble(6, d.getDescuento());
                    psDetalle.setDouble(7, d.getIva());
                    psDetalle.setDouble(8, d.getTotal());
                    psDetalle.setInt(9, 1);
                    psDetalle.addBatch();
                    
                    // Stock
                    psStock.setInt(1, d.getCantidad());
                    psStock.setInt(2, d.getIdProducto());
                    psStock.addBatch();

                    // Movimiento Inventario
                    psMovimiento.setInt(1, d.getIdProducto());
                    psMovimiento.setInt(2, d.getCantidad());
                    psMovimiento.addBatch();
                }
                
                // Ejecutar lotes
                psDetalle.executeBatch();
                psStock.executeBatch();
                psMovimiento.executeBatch();
                
                // Confirmar transacción
                cn.commit();
                respuesta = true;
            } else {
                cn.rollback();
            }
            
        } catch (SQLException e) {
            System.out.println("Error al guardar venta: " + e);
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
}
