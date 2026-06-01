/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class conexion {
    
    // Mantenemos una única conexión física abierta y el tiempo de su última validación
    private static Connection realConnection = null;
    private static long ultimaValidacion = 0;
    
    public static Connection conectar() {
        try {
            boolean isValida = false;
            if (realConnection != null) {
                try {
                    if (realConnection.isClosed()) {
                        isValida = false;
                    } else {
                        // Solo hacemos el ping (isValid) si pasaron más de 5 segundos desde la última vez
                        long ahora = System.currentTimeMillis();
                        if (ahora - ultimaValidacion > 5000) {
                            isValida = realConnection.isValid(2);
                            if (isValida) ultimaValidacion = ahora;
                        } else {
                            isValida = true;
                        }
                    }
                } catch (Exception e) {
                    isValida = false; // Si lanza excepción, la consideramos inválida
                }
            }

            // Si la conexión no es válida, intentamos reconectar
            if (!isValida) {
                if (realConnection != null) {
                    try { realConnection.close(); } catch (Exception e) {}
                }
                
                // Creamos la conexión física real
                realConnection = DriverManager.getConnection(
                        "jdbc:mysql://46.183.117.154:3306/posjj?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota&sessionVariables=time_zone='-05:00'",
                        "admin",
                        "admin0"
                );
                System.out.println("Nueva conexión física establecida al VPS.");
            }
            
            // Retornamos un Proxy para interceptar el método close()
            return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] { Connection.class },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("close".equals(method.getName())) {
                            // Ignoramos el cierre lógico para mantener la conexión física abierta
                            return null;
                        }
                        // Cualquier otro método se delega a la conexión real
                        return method.invoke(realConnection, args);
                    }
                }
            );
            
        } catch (SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
            return null;
        }
    } 

    public static void main(String[] args) {
        Connection cn = conectar();
        if (cn != null) {
            System.out.println("Base de datos lista.");
            try { cn.close(); } catch (SQLException e) { }
            
            try {
                if (!realConnection.isClosed()) {
                    System.out.println("Prueba superada: La conexión física sigue abierta.");
                }
            } catch (SQLException e) { }
        } else {
            System.out.println("Revisa usuario, contraseña o nombre de BD.");
        }
    }
}
