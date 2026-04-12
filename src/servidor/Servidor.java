// Arquitectura Cliente-Servidor - Guía 6
// Servidor: se registra en el Registry y atiende solicitudes del cliente
package servidor;
import java.io.*;
import java.net.*;

public class Servidor {

    public static void main(String[] args) {

        String nombreServicio = "servicioBD"; // Nombre lógico del servicio
        String direccion = "localhost:5000";  // Dirección física del servidor

        // 🔹 Registro automático en el Registry (BIND)
        registrarEnRegistry(nombreServicio, direccion);

        // 🔹 Servidor que escucha peticiones del cliente
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Servidor listo en puerto 5000");

            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("Cliente conectado");

                // Enviar respuesta al cliente
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
                out.println("Respuesta del servidor");

                cliente.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método que registra el servicio en el Registry
    private static void registrarEnRegistry(String nombre, String direccion) {
        try (
            Socket socket = new Socket("localhost", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Enviar operación BIND al Registry
            out.println("BIND " + nombre + " " + direccion);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}