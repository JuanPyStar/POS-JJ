// Arquitectura Cliente-Servidor - Guía 6
// Módulo Registry: encargado de registrar y localizar servicios remotos
package servidor;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class RegistryServer {

    // Estructura de datos que almacena: nombreServicio → direccion(IP:Puerto)
    private static Map<String, String> servicios = new HashMap<>();

    public static void main(String[] args) {
        int puerto = 6000; // Puerto donde escucha el Registry

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Registry corriendo en puerto " + puerto);

            // El Registry siempre está activo esperando solicitudes
            while (true) {
                Socket cliente = serverSocket.accept();

                // Se atiende cada cliente en un hilo independiente
                new Thread(() -> manejarCliente(cliente)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método que procesa las solicitudes (BIND o LOOKUP)
    private static void manejarCliente(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Leer la solicitud enviada por el cliente o servidor
            String solicitud = in.readLine();

            // 🔹 Operación BIND (registro de servicio)
            if (solicitud.startsWith("BIND")) {
                // Formato: BIND nombreServicio IP:Puerto
                String[] partes = solicitud.split(" ");

                // Se guarda o actualiza el servicio en el mapa
                servicios.put(partes[1], partes[2]);

                out.println("OK");
                System.out.println("Servicio registrado: " + partes[1]);

            // 🔹 Operación LOOKUP (búsqueda de servicio)
            } else if (solicitud.startsWith("LOOKUP")) {
                // Formato: LOOKUP nombreServicio
                String[] partes = solicitud.split(" ");
                String direccion = servicios.get(partes[1]);

                // Se responde con la dirección o un mensaje si no existe
                if (direccion != null) {
                    out.println(direccion);
                } else {
                    out.println("NO_ENCONTRADO");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}