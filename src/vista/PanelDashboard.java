package vista;

import controlador.Ctrl_Dashboard;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Usuario;

public class PanelDashboard extends JPanel {

    private Usuario usuario;
    private Menu menuPrincipal;
    
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public PanelDashboard(Usuario usuario, Menu menuPrincipal) {
        this.usuario = usuario;
        this.menuPrincipal = menuPrincipal;
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(30, 30, 30, 30));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Bienvenido al Dashboard, " + usuario.getNombre());
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 32));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JLabel lblSubtitulo = new JLabel("Resumen estadístico de hoy");
        lblSubtitulo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
        lblSubtitulo.setForeground(Color.GRAY);

        JPanel panelTextosHeader = new JPanel(new GridLayout(2, 1));
        panelTextosHeader.setBackground(colorFondo);
        panelTextosHeader.add(lblTitulo);
        panelTextosHeader.add(lblSubtitulo);

        panelHeader.add(panelTextosHeader, BorderLayout.WEST);
        
        // Botón para recargar datos manualmente
        JButton btnRecargar = new JButton("Actualizar Datos");
        btnRecargar.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        btnRecargar.setBackground(colorAzulPrincipal);
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setFocusPainted(false);
        btnRecargar.addActionListener(e -> {
            this.removeAll();
            inicializarComponentes();
            this.revalidate();
            this.repaint();
        });
        panelHeader.add(btnRecargar, BorderLayout.EAST);
        
        this.add(panelHeader, BorderLayout.NORTH);

        // --- CARGAR DATOS REALES ---
        Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
        int ventasHoy = ctrl.getTotalVentasDia();
        double ingresosHoy = ctrl.getTotalIngresosDia();
        int totalProductos = ctrl.getTotalProductos();
        int totalUsuarios = ctrl.getTotalUsuarios();

        // --- CARDS ESTADÍSTICAS ---
        JPanel panelCards = new JPanel(new GridLayout(1, 4, 20, 0));
        panelCards.setBackground(colorFondo);
        panelCards.setPreferredSize(new Dimension(0, 150));
        
        // Se crean las cards y se les asigna la navegación
        panelCards.add(crearCardClickeable("Ventas del Día", String.valueOf(ventasHoy), new Color(46, 204, 113), "VentasDelDia"));
        panelCards.add(crearCardClickeable("Total Productos", String.valueOf(totalProductos), colorAzulPrincipal, "Productos"));
        panelCards.add(crearCardClickeable("Ingresos Netos", String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", ingresosHoy), new Color(155, 89, 182), "Reportes"));
        panelCards.add(crearCardClickeable("Usuarios Activos", String.valueOf(totalUsuarios), new Color(241, 196, 15), "Usuarios"));

        // --- GRÁFICAS ---
        JPanel panelGraficas = new JPanel(new BorderLayout());
        panelGraficas.setBackground(Color.WHITE);
        panelGraficas.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblGrafica = new JLabel("Ventas Semanales", SwingConstants.LEFT);
        lblGrafica.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 22));
        lblGrafica.setForeground(colorTexto);
        panelGraficas.add(lblGrafica, BorderLayout.NORTH);

        List<Object[]> ventasSemanales = ctrl.getVentasSemanales();
        panelGraficas.add(crearPanelGraficaVentasSemanales(ventasSemanales), BorderLayout.CENTER);

        // --- ENSAMBLAJE CENTRO ---
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setBackground(colorFondo);

        panelCentro.add(panelCards);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 30)));
        panelCentro.add(panelGraficas);

        this.add(panelCentro, BorderLayout.CENTER);
    }

    private JPanel crearCardClickeable(String titulo, String valor, Color colorBorde, String destinoPanel) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(5, 1, 1, 1, colorBorde), // Borde superior grueso
            new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.GRAY);
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 18));

        JLabel lblValor = new JLabel(valor);
        lblValor.setForeground(colorTexto);
        lblValor.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 36));

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        // Evento de clic y efecto Hover
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                menuPrincipal.navegarA(destinoPanel);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(245, 250, 255)); // Ligeramente azul al pasar el mouse
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }
        });

        return panel;
    }

    private JPanel crearPanelGraficaVentasSemanales(List<Object[]> datosVentas) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int ancho = getWidth();
                int alto = getHeight();
                int padding = 50;
                int textoAltura = 18;
                g.setColor(new Color(250, 250, 250));
                g.fillRect(0, 0, ancho, alto);

                if (datosVentas == null || datosVentas.isEmpty()) {
                    g.setColor(Color.GRAY);
                    g.setFont(new Font("Yu Gothic UI", Font.ITALIC, 18));
                    g.drawString("No hay ventas registradas en los últimos 7 días.", padding, alto / 2);
                    return;
                }

                double maxValor = 0;
                for (Object[] fila : datosVentas) {
                    double valor = (double) fila[1];
                    if (valor > maxValor) {
                        maxValor = valor;
                    }
                }
                if (maxValor == 0) {
                    maxValor = 1;
                }

                int disponibleAncho = ancho - padding * 2;
                int disponibleAlto = alto - padding * 2 - textoAltura * 2;
                int barraAncho = Math.max(30, disponibleAncho / Math.max(1, datosVentas.size()) - 10);
                int espacioEntreBarras = (disponibleAncho - barraAncho * datosVentas.size()) / Math.max(1, datosVentas.size() - 1);

                g.setColor(new Color(220, 220, 220));
                g.drawLine(padding, alto - padding - textoAltura, ancho - padding, alto - padding - textoAltura);

                for (int i = 0; i < datosVentas.size(); i++) {
                    Object[] fila = datosVentas.get(i);
                    String etiqueta = fila[0].toString();
                    double valor = (double) fila[1];
                    int alturaBarra = (int) ((valor / maxValor) * disponibleAlto);

                    int x = padding + i * (barraAncho + espacioEntreBarras);
                    int y = alto - padding - textoAltura - alturaBarra;

                    g.setColor(new Color(102, 153, 255));
                    g.fillRect(x, y, barraAncho, alturaBarra);
                    g.setColor(new Color(30, 30, 30));
                    g.drawRect(x, y, barraAncho, alturaBarra);

                    g.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
                    FontMetrics metrics = g.getFontMetrics();
                    int labelWidth = metrics.stringWidth(etiqueta);
                    g.drawString(etiqueta, x + Math.max(0, (barraAncho - labelWidth) / 2), alto - padding);

                    String valorTexto = String.format("$%.0f", valor);
                    int valorTextoWidth = metrics.stringWidth(valorTexto);
                    g.drawString(valorTexto, x + Math.max(0, (barraAncho - valorTextoWidth) / 2), y - 8);
                }
            }
        };
        panel.setPreferredSize(new Dimension(0, 260));
        panel.setBackground(Color.WHITE);
        return panel;
    }
}
