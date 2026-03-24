/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class conexion {
    
    public static Connection conectar() {
        Connection cn = null;
        try {
            cn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bd_posjj",
                    "root",
                    "root123"
            );
            System.out.println("Conexión exitosa");
        } catch (SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }
        return cn;
    } 

    // ✅ AGREGA ESTO TEMPORALMENTE PARA PROBAR
    public static void main(String[] args) {
        Connection cn = conectar();
        if (cn != null) {
            System.out.println("Base de datos lista.");
            try { cn.close(); } catch (SQLException e) { }
        } else {
            System.out.println("Revisa usuario, contraseña o nombre de BD.");
        }
    }
    
        
    
}

