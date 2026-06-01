package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import conexion.conexion;
import modelo.Turno;

public class Ctrl_Turno {

    // Abrir turno
    public boolean abrirTurno(Turno turno) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "INSERT INTO tb_turno (idUsuario, fechaApertura, baseInicial, totalVentas, estado) VALUES (?, NOW(), ?, 0, 1)"
            );
            consulta.setInt(1, turno.getIdUsuario());
            consulta.setDouble(2, turno.getBaseInicial());

            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al abrir turno: " + e);
        }
        return respuesta;
    }

    // Cerrar turno
    public boolean cerrarTurno(int idTurno, double totalVentas) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "UPDATE tb_turno SET fechaCierre = NOW(), totalVentas = ?, estado = 0 WHERE idTurno = ?"
            );
            consulta.setDouble(1, totalVentas);
            consulta.setInt(2, idTurno);

            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar turno: " + e);
        }
        return respuesta;
    }

    // Obtener turno activo
    public Turno getTurnoActivo() {
        Turno turno = null;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "SELECT * FROM tb_turno WHERE estado = 1 ORDER BY idTurno DESC LIMIT 1"
            );
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                turno = new Turno();
                turno.setIdTurno(rs.getInt("idTurno"));
                turno.setIdUsuario(rs.getInt("idUsuario"));
                turno.setFechaApertura(rs.getString("fechaApertura"));
                turno.setFechaCierre(rs.getString("fechaCierre"));
                turno.setBaseInicial(rs.getDouble("baseInicial"));
                turno.setTotalVentas(rs.getDouble("totalVentas"));
                turno.setEstado(rs.getInt("estado"));
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener turno activo: " + e);
        }
        return turno;
    }

    // Calcular ventas del turno activo
    public double calcularVentasTurno(int idTurno) {
        double total = 0.0;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "SELECT SUM(totalPagar) as total FROM tb_factura WHERE idTurno = ? AND estado = 1"
            );
            consulta.setInt(1, idTurno);
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al calcular ventas turno: " + e);
        }
        return total;
    }
}
