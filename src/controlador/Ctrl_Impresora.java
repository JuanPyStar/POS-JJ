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
            
            // 3. Generar Ticket de Texto Plano (48 columnas para 80mm)
            StringBuilder ticket = new StringBuilder();
            
            ticket.append(repetir("=", 48)).append("\n");
            ticket.append(centrar(empNombre, 48)).append("\n");
            ticket.append(centrar("SISTEMA DE VENTAS", 48)).append("\n");
            ticket.append(repetir("=", 48)).append("\n\n");
            
            ticket.append("NIT: ").append(empNit).append("\n");
            ticket.append("Dirección: ").append(empDir).append("\n");
            ticket.append("Teléfono: ").append(empTel).append("\n\n");
            
            ticket.append(repetir("-", 48)).append("\n");
            ticket.append("FACTURA DE VENTA N° ").append(numFactura).append("\n");
            ticket.append("Fecha: ").append(fecha).append("\n");
            ticket.append("Hora: ").append(hora).append("\n");
            ticket.append(repetir("-", 48)).append("\n\n");
            
            ticket.append("DATOS DEL CLIENTE\n");
            ticket.append("Cliente: ").append(cliNombre).append("\n");
            ticket.append("Documento: ").append(cliDoc).append("\n\n");
            
            ticket.append(repetir("-", 48)).append("\n");
            ticket.append(centrar("DESCRIPCIÓN DE PRODUCTOS", 48)).append("\n");
            ticket.append(repetir("-", 48)).append("\n");
            
            // Cabecera: Producto (22), Cant (4), Precio (10), Total (10) + 2 espacios = 48
            ticket.append(alinearIzq("Producto", 22)).append(" ")
                  .append(alinearDer("Cant", 4)).append(" ")
                  .append(alinearDer("Precio", 10)).append(" ")
                  .append(alinearDer("Total", 10)).append("\n");
            ticket.append(repetir("-", 48)).append("\n");
            
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
                
                if (nom.length() > 22) nom = nom.substring(0, 19) + "...";
                
                String strCant = String.valueOf(cant);
                String strPrecio = formatoMoneda(precioU);
                String strSub = formatoMoneda(subT);
                
                ticket.append(alinearIzq(nom, 22)).append(" ")
                      .append(alinearDer(strCant, 4)).append(" ")
                      .append(alinearDer(strPrecio, 10)).append(" ")
                      .append(alinearDer(strSub, 10)).append("\n");
            }
            ticket.append(repetir("-", 48)).append("\n\n");
            
            ticket.append(alinearIzq("Subtotal:", 25)).append(alinearDer(formatoMoneda(subtotal), 23)).append("\n");
            ticket.append(alinearIzq("Descuento:", 25)).append(alinearDer(formatoMoneda(totalDescuento), 23)).append("\n");
            ticket.append(alinearIzq("IVA:", 25)).append(alinearDer(formatoMoneda(iva), 23)).append("\n");
            ticket.append(repetir("-", 48)).append("\n");
            ticket.append(alinearIzq("TOTAL A PAGAR:", 25)).append(alinearDer(formatoMoneda(total), 23)).append("\n");
            ticket.append(repetir("-", 48)).append("\n\n");
            
            ticket.append("Método de pago: ").append(metodoPago).append("\n");
            ticket.append(alinearIzq("Recibido:", 25)).append(alinearDer(formatoMoneda(valorPagado), 23)).append("\n");
            ticket.append(alinearIzq("Cambio:", 25)).append(alinearDer(formatoMoneda(valorPagado - total), 23)).append("\n\n");
            
            ticket.append(repetir("=", 48)).append("\n");
            ticket.append(centrar(empMensaje, 48)).append("\n");
            ticket.append(centrar("Vuelva pronto a POS JJ", 48)).append("\n");
            ticket.append(repetir("=", 48)).append("\n");
            
            // Fin de papel
            ticket.append("\n\n\n\n\n\n");
            
            cn.close();
            
            // 4. Mandar a imprimir sin preguntar
            java.net.URL urlLogo = Ctrl_Impresora.class.getResource("/img/posjj-removebg-preview.png");
            String imgTag = "";
            if (urlLogo != null) {
                imgTag = "<div style='text-align: center;'><img src='" + urlLogo.toString() + "' width='100'></div><br>";
            }
            
            String ticketHtml = ticket.toString().replace(" ", "&nbsp;").replace("\n", "<br>");
            String htmlContent = "<html><body style='margin: 0; padding: 0;'>" + 
                                 imgTag + 
                                 "<div style='font-family: monospace; font-size: 10px; width: 250px;'>" + 
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
