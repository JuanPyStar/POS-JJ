package controlador;

import conexion.conexion;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import javax.swing.JTextPane;

public class Ctrl_Impresora {
    
    public static void imprimirFactura(int idFactura) {
        Connection cn = conexion.conectar();
        if (cn == null) return;
        
        try {
            // 1. Cargar configuración
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("config.properties")) {
                props.load(in);
            } catch (Exception e) {}
            
            String empNombre = props.getProperty("empresa_nombre", "POS JJ");
            String empNit = props.getProperty("empresa_nit", "NIT: 000000000");
            String empTel = props.getProperty("empresa_telefono", "TEL: 0000000");
            String empDir = props.getProperty("empresa_direccion", "DIR: SIN DIRECCION");
            String empMensaje = props.getProperty("empresa_mensaje", "¡Gracias por su compra!");
            
            // 2. Cargar datos de la factura
            String numFactura = "";
            String fecha = "";
            String hora = "";
            double subtotal = 0;
            double iva = 0;
            double total = 0;
            String metodoPago = "";
            double valorPagado = 0;
            String cajero = "";
            String cliNombre = "Consumidor Final";
            String cliDoc = "2222222222";
            
            String sqlFactura = "SELECT f.numeroFactura, f.fechaFactura, f.subtotal, f.totalIva, f.totalPagar, " +
                                "p.metodoPago, p.valorPagado, u.nombre AS cajeroN, u.apellido AS cajeroA, " +
                                "c.nombre AS cliN, c.apellido AS cliA, c.cedula AS cliDoc " +
                                "FROM tb_factura f " +
                                "LEFT JOIN tb_pago p ON f.idFactura = p.idFactura " +
                                "INNER JOIN tb_usuario u ON f.idUsuario = u.idUsuario " +
                                "LEFT JOIN tb_cliente c ON f.idCliente = c.idCliente " +
                                "WHERE f.idFactura = ?";
                                
            PreparedStatement ps1 = cn.prepareStatement(sqlFactura);
            ps1.setInt(1, idFactura);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                numFactura = rs1.getString("numeroFactura");
                String fechaFull = rs1.getString("fechaFactura");
                if (fechaFull != null && fechaFull.contains(" ")) {
                    fecha = fechaFull.split(" ")[0];
                    hora = fechaFull.split(" ")[1];
                }
                subtotal = rs1.getDouble("subtotal");
                iva = rs1.getDouble("totalIva");
                total = rs1.getDouble("totalPagar");
                metodoPago = rs1.getString("metodoPago") != null ? rs1.getString("metodoPago") : "N/A";
                valorPagado = rs1.getDouble("valorPagado");
                if (valorPagado < total) valorPagado = total; // Ajuste si no se guardó el recibido real
                
                cajero = rs1.getString("cajeroN") + " " + rs1.getString("cajeroA");
                if (rs1.getString("cliN") != null) {
                    cliNombre = rs1.getString("cliN") + " " + rs1.getString("cliA");
                    cliDoc = rs1.getString("cliDoc");
                }
            }
            
            // 3. Generar Ticket de Texto Plano (32 columnas adaptable)
            StringBuilder ticket = new StringBuilder();
            
            ticket.append(repetir("=", 32)).append("\n");
            ticket.append(centrar(empNombre, 32)).append("\n");
            ticket.append(centrar("SISTEMA DE VENTAS", 32)).append("\n");
            ticket.append(repetir("=", 32)).append("\n\n");
            
            ticket.append("NIT: ").append(empNit).append("\n");
            ticket.append("DIR: ").append(empDir).append("\n");
            ticket.append("TEL: ").append(empTel).append("\n\n");
            
            ticket.append(repetir("-", 32)).append("\n");
            ticket.append("FACTURA N° ").append(numFactura).append("\n");
            ticket.append("FECHA: ").append(fecha).append(" ").append(hora).append("\n");
            ticket.append(repetir("-", 32)).append("\n\n");
            
            ticket.append("CLIENTE: ").append(cliNombre).append("\n");
            ticket.append("DOC: ").append(cliDoc).append("\n\n");
            
            ticket.append(repetir("-", 32)).append("\n");
            ticket.append(centrar("PRODUCTOS", 32)).append("\n");
            ticket.append(repetir("-", 32)).append("\n");
            
            ticket.append(alinearIzq("ARTICULO", 11)).append(" ")
                  .append(alinearDer("C.", 2)).append(" ")
                  .append(alinearDer("PRECIO", 8)).append(" ")
                  .append(alinearDer("TOTAL", 8)).append("\n");
            ticket.append(repetir("-", 32)).append("\n");
            
            String sqlDetalles = "SELECT p.nombre, d.cantidad, d.precioUnitario, d.subtotal, d.descuento " +
                                 "FROM tb_detalle_factura d " +
                                 "INNER JOIN tb_producto p ON d.idProducto = p.idProducto " +
                                 "WHERE d.idFactura = ?";
            PreparedStatement ps2 = cn.prepareStatement(sqlDetalles);
            ps2.setInt(1, idFactura);
            ResultSet rs2 = ps2.executeQuery();
            
            double totalDescuento = 0;
            
            while (rs2.next()) {
                String nom = rs2.getString("nombre");
                int cant = rs2.getInt("cantidad");
                double precioU = rs2.getDouble("precioUnitario");
                double subT = rs2.getDouble("subtotal");
                totalDescuento += rs2.getDouble("descuento");
                
                if (nom.length() > 11) nom = nom.substring(0, 10) + ".";
                
                String strCant = String.valueOf(cant);
                String strPrecio = formatoMoneda(precioU).replace(" ", "");
                String strSub = formatoMoneda(subT).replace(" ", "");
                
                ticket.append(alinearIzq(nom, 11)).append(" ")
                      .append(alinearDer(strCant, 2)).append(" ")
                      .append(alinearDer(strPrecio, 8)).append(" ")
                      .append(alinearDer(strSub, 8)).append("\n");
            }
            ticket.append(repetir("-", 32)).append("\n\n");
            
            ticket.append(alinearIzq("Subtotal:", 14)).append(alinearDer(formatoMoneda(subtotal), 18)).append("\n");
            ticket.append(alinearIzq("Descuento:", 14)).append(alinearDer(formatoMoneda(totalDescuento), 18)).append("\n");
            ticket.append(alinearIzq("IVA:", 14)).append(alinearDer(formatoMoneda(iva), 18)).append("\n");
            ticket.append(repetir("-", 32)).append("\n");
            ticket.append(alinearIzq("TOTAL A PAGAR:", 14)).append(alinearDer(formatoMoneda(total), 18)).append("\n");
            ticket.append(repetir("-", 32)).append("\n\n");
            
            ticket.append("Medio: ").append(metodoPago).append("\n");
            ticket.append(alinearIzq("Recibido:", 14)).append(alinearDer(formatoMoneda(valorPagado), 18)).append("\n");
            ticket.append(alinearIzq("Cambio:", 14)).append(alinearDer(formatoMoneda(valorPagado - total), 18)).append("\n\n");
            
            ticket.append(repetir("=", 32)).append("\n");
            ticket.append(centrar(empMensaje, 32)).append("\n");
            ticket.append(centrar("Vuelva pronto", 32)).append("\n");
            ticket.append(repetir("=", 32)).append("\n");
            
            // Fin de papel
            ticket.append("\n\n\n\n\n\n");
            
            cn.close();
            
            // 4. Mandar a imprimir sin preguntar
            String imgTag = ""; // Logo retirado
            
            String ticketHtml = ticket.toString().replace(" ", "&nbsp;").replace("\n", "<br>");
            String htmlContent = "<html><body style='margin: 0; padding: 0;'>" + 
                                 imgTag + 
                                 "<div style='font-family: monospace; font-size: 8px; width: 170px;'>" + 
                                 ticketHtml + 
                                 "</div></body></html>";
                                 
            JTextPane textPane = new JTextPane();
            textPane.setContentType("text/html");
            textPane.setText(htmlContent);
            
            try {
                // print(header, footer, showPrintDialog, service, attributes, interactive)
                textPane.print(null, null, false, null, null, false);
            } catch (Exception e) {
                System.out.println("Error al imprimir: " + e);
            }
            
        } catch (Exception e) {
            System.out.println("Error en Ctrl_Impresora: " + e);
        }
    }
    
    // --- Utilidades de formato ASCII ---
    
    private static String formatoMoneda(double valor) {
        return String.format(java.util.Locale.forLanguageTag("es-CO"), "$%,.0f", valor);
    }
    
    private static String repetir(String str, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) sb.append(str);
        return sb.toString();
    }
    
    private static String centrar(String texto, int ancho) {
        if (texto.length() >= ancho) return texto.substring(0, ancho);
        int padIzq = (ancho - texto.length()) / 2;
        int padDer = ancho - texto.length() - padIzq;
        return repetir(" ", padIzq) + texto + repetir(" ", padDer);
    }
    
    private static String alinearIzq(String texto, int ancho) {
        if (texto.length() >= ancho) return texto.substring(0, ancho);
        return texto + repetir(" ", ancho - texto.length());
    }
    
    private static String alinearDer(String texto, int ancho) {
        if (texto.length() >= ancho) return texto.substring(0, ancho);
        return repetir(" ", ancho - texto.length()) + texto;
    }
}
