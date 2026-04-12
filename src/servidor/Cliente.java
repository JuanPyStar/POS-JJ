package servidor;
// Arquitectura Cliente-Servidor - Guía 6
// Cliente: consulta el Registry para localizar el servicio dinámicamente
import java.io.*;
import java.net.*;

public class Cliente {

    public static void main(String[] args) {

        // 🔹 Solicitar al Registry la dirección del servicio (LOOKUP)
        String direccion = consultarRegistry("servicioBD");

        // Validar si el servicio existe
        if (direccion.equals("NO_ENCONTRADO")) {
            System.out.println("Servicio no encontrado");
            return;
        }

        // Separar IP y puerto
        String[] partes = direccion.split(":");
        String ip = partes[0];
        int puerto = Integer.parseInt(partes[1]);

        // 🔹 Conexión dinámica al servidor (sin IP fija)
        try (Socket socket = new Socket(ip, puerto)) {

            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            // Leer respuesta del servidor
            String respuesta = in.readLine();
            System.out.println("Servidor dice: " + respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método que consulta el Registry
    private static String consultarRegistry(String nombreServicio) {
        try (
            Socket socket = new Socket("localhost", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            )
        ) {
            // Enviar operación LOOKUP
            out.println("LOOKUP " + nombreServicio);

            // Retornar la dirección obtenida
            return in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NO_ENCONTRADO";
    }
}
