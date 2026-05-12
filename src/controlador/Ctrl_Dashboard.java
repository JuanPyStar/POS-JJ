package controlador;

import conexion.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
