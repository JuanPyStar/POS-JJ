package vista;

import conexion.conexion;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Usuario;

public class PanelResumenVentas extends JPanel {

    private Usuario usuarioLogueado;
    private Menu menuPrincipal;

    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorVerde = new Color(46, 204, 113);

    private JLabel lblTotalHoy;
    private ChartPanel chartPanel;

    private List<VentaDia> datosGrafica;

    public PanelResumenVentas(Usuario usuario, Menu menu) {
        this.usuarioLogueado = usuario;
        this.menuPrincipal = menu;
        this.datosGrafica = new ArrayList<>();

        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(colorFondo);
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
    }

    public void refrescarDatos() {
        cargarDatosHoy();
        cargarDatosGrafica();
        chartPanel.repaint();
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(colorFondo);

        JLabel lblTitulo = new JLabel("Resumen de Ventas");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);

        lblTotalHoy = new JLabel("Total del día: $ 0");
        lblTotalHoy.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 24));
        lblTotalHoy.setForeground(colorVerde);

        JPanel pnlTitulos = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlTitulos.setBackground(colorFondo);
        pnlTitulos.add(lblTitulo);

        JLabel lblInstruccion = new JLabel("(Haz clic en la gráfica para ver el detalle de todas las facturas)");
        lblInstruccion.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        lblInstruccion.setForeground(Color.GRAY);
        pnlTitulos.add(lblInstruccion);

        panelNorte.add(pnlTitulos, BorderLayout.WEST);
        panelNorte.add(lblTotalHoy, BorderLayout.EAST);

        this.add(panelNorte, BorderLayout.NORTH);

        // Panel para la Gráfica
        chartPanel = new ChartPanel();
        chartPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navegar a Historial refrescando datos
                menuPrincipal.mostrarHistorial();
            }
        });

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(colorFondo);
        panelCentro.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Ventas de los últimos 7 días"));
        panelCentro.add(chartPanel, BorderLayout.CENTER);

        this.add(panelCentro, BorderLayout.CENTER);
    }

    private void cargarDatosHoy() {
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT SUM(totalPagar) as totalHoy FROM tb_factura WHERE estado = 1";

            boolean isAdmin = usuarioLogueado.getRol() != null && (usuarioLogueado.getRol().equalsIgnoreCase("Administrador") || usuarioLogueado.getRol().equalsIgnoreCase("Admin"));
            if (!isAdmin) {
                sql += " AND idUsuario = ?";
            }

            PreparedStatement ps = cn.prepareStatement(sql);
            if (!isAdmin) {
                ps.setInt(1, usuarioLogueado.getIdUsuario());
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("totalHoy");
                lblTotalHoy.setText(
                        "Total Ventas: " + String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", total));
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error Ventas Hoy: " + e);
        }
    }

    private void cargarDatosGrafica() {
        datosGrafica.clear();
        Connection cn = conexion.conectar();
        try {
            // Últimos 7 días con ventas registradas
            String sql = "SELECT DATE(fechaFactura) as fecha, SUM(totalPagar) as total " +
                         "FROM tb_factura " +
                         "WHERE estado = 1 ";

            boolean isAdmin = usuarioLogueado.getRol() != null && (usuarioLogueado.getRol().equalsIgnoreCase("Administrador") || usuarioLogueado.getRol().equalsIgnoreCase("Admin"));
            if (!isAdmin) {
                sql += "AND idUsuario = ? ";
            }

            sql += "GROUP BY DATE(fechaFactura) ORDER BY DATE(fechaFactura) DESC LIMIT 7";

            PreparedStatement ps = cn.prepareStatement(sql);
            if (!isAdmin) {
                ps.setInt(1, usuarioLogueado.getIdUsuario());
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                datosGrafica.add(new VentaDia(rs.getString("fecha"), rs.getDouble("total")));
            }
            cn.close();

            // Invertir la lista para que la fecha más antigua quede a la izquierda
            Collections.reverse(datosGrafica);
        } catch (SQLException e) {
            System.out.println("Error Gráfica: " + e);
        }
    }

    // Clase interna para guardar el par Fecha - Total
    private class VentaDia {
        String fecha;
        double total;

        VentaDia(String fecha, double total) {
            this.fecha = fecha;
            this.total = total;
        }
    }

    // Panel personalizado para dibujar la gráfica de barras
    private class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 50;

            // Fondo
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            if (datosGrafica.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
                g2.drawString("No hay datos de ventas recientes", width / 2 - 100, height / 2);
                return;
            }

            // Calcular el máximo
            double maxVal = 0;
            for (VentaDia v : datosGrafica) {
                if (v.total > maxVal)
                    maxVal = v.total;
            }
            if (maxVal == 0)
                maxVal = 10000; // Evitar división por cero

            // Dibujar ejes
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(padding, height - padding, width - padding, height - padding); // Eje X
            g2.drawLine(padding, height - padding, padding, padding); // Eje Y

            // Dibujar barras
            int barWidth = (width - 2 * padding) / datosGrafica.size() - 20;
            if (barWidth < 20)
                barWidth = 20;
            if (barWidth > 80)
                barWidth = 80;

            int x = padding + 20;
            g2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

            for (VentaDia v : datosGrafica) {
                int barHeight = (int) ((v.total / maxVal) * (height - 2 * padding));
                int y = height - padding - barHeight;

                // Barra
                g2.setColor(new Color(102, 153, 255, 200)); // Azul semitransparente
                g2.fillRect(x, y, barWidth, barHeight);
                g2.setColor(colorAzulPrincipal);
                g2.drawRect(x, y, barWidth, barHeight);

                // Texto Fecha (Eje X)
                g2.setColor(Color.DARK_GRAY);
                String[] partes = v.fecha.split("-");
                String fechaCorta = partes.length == 3 ? partes[2] + "/" + partes[1] : v.fecha;
                int textWidth = g2.getFontMetrics().stringWidth(fechaCorta);
                g2.drawString(fechaCorta, x + (barWidth - textWidth) / 2, height - padding + 20);

                // Texto Total arriba de la barra
                String strTotal = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", v.total);
                int totalWidth = g2.getFontMetrics().stringWidth(strTotal);
                g2.drawString(strTotal, x + (barWidth - totalWidth) / 2, y - 5);

                x += barWidth + 20;
            }
        }
    }
}
