/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

/**
 *
 * @author JDJju
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class conexion {
    public static Connection conectar() {

        Connection cn = null;

        try {

            cn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bd_posjj",
                    "root",          // usuario MySQL
                    "root123"               // contraseña MySQL
            );

            System.out.println("Conexion exitosa");

        } catch (SQLException e) {

            System.out.println("Error en la conexion local " + e);

        }

        return cn;
    }
}
