package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Usuario;

public class Menu extends JFrame {

    private Usuario usuarioLogueado;
    
    // Paneles principales
    private JPanel panelLateral;
    private JPanel panelContenido;
    private CardLayout cardLayout;
    
    private PanelHistorialFacturas panelHistorial;
    private PanelDetalleFactura panelDetalle;
    private PanelResumenVentas panelResumen;
    private PanelVentasDelDia panelVentasDelDia;

    // Colores del DISEÑO ORIGINAL DEL USUARIO (Login)
    private Color colorFondoLateral = new Color(102, 153, 255); // Azul claro
    private Color colorFondoPrincipal = new Color(255, 255, 255); // Blanco
    private Color colorTexto = new Color(255, 255, 255); // Blanco para sidebar
    private Color colorTextoOscuro = new Color(50, 50, 50);

    // Botones del menú
    private JButton btnDashboard, btnVentas, btnResumenVentas, btnHistorial, btnProductos, btnUsuarios, btnReportes, btnInventario, btnConfiguracion, btnCerrarSesion;
    
    // Etiquetas de categoría para poder ocultarlas
    private JLabel lblCatPrincipal, lblCatModulos, lblCatAdmin;

    public Menu(Usuario usuario) {
        this.usuarioLogueado = usuario;
        
        // Configuración de la ventana principal
        this.setTitle("POS JJ - Sistema de Gestión");
        this.setSize(1200, 700);
        this.setMinimumSize(new Dimension(1000, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        inicializarComponentes();
        configurarRoles();
    }

    private void inicializarComponentes() {
        // ================= PANEL LATERAL (SIDEBAR AZUL) =================
        panelLateral = new JPanel();
        panelLateral.setPreferredSize(new Dimension(250, 0));
        panelLateral.setBackground(colorFondoLateral);
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Logo y Usuario
        JLabel lblLogo = new JLabel("POS JJ", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 32));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        String rol = usuarioLogueado.getRol() != null ? usuarioLogueado.getRol().toUpperCase() : "CAJERO";
        JLabel lblRol = new JLabel(rol, SwingConstants.CENTER);
        lblRol.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        lblRol.setForeground(new Color(230, 240, 255));
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelLateral.add(lblLogo);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 5)));
        panelLateral.add(lblRol);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 40)));

        // Creación de botones del menú
        lblCatPrincipal = crearEtiquetaCategoria("PRINCIPAL");
        panelLateral.add(lblCatPrincipal);
        btnDashboard = crearBotonMenu("Dashboard");
        panelLateral.add(btnDashboard);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 15)));

        lblCatModulos = crearEtiquetaCategoria("MÓDULOS");
        panelLateral.add(lblCatModulos);
        btnVentas = crearBotonMenu("Punto de Venta");
        btnResumenVentas = crearBotonMenu("Ventas");
        btnHistorial = crearBotonMenu("Historial Facturas");
        btnInventario = crearBotonMenu("Inventario");
        btnProductos = crearBotonMenu("Gestión Productos");
        btnUsuarios = crearBotonMenu("Usuarios");
        
        panelLateral.add(btnVentas);
        panelLateral.add(btnResumenVentas);
        panelLateral.add(btnHistorial);
        panelLateral.add(btnInventario);
        panelLateral.add(btnProductos);
        panelLateral.add(btnUsuarios);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 15)));

        lblCatAdmin = crearEtiquetaCategoria("ADMINISTRACIÓN");
        panelLateral.add(lblCatAdmin);
        btnReportes = crearBotonMenu("Reportes");
        btnConfiguracion = crearBotonMenu("Configuración");
        panelLateral.add(btnReportes);
        panelLateral.add(btnConfiguracion);
        
        panelLateral.add(Box.createVerticalGlue()); // Empujar "Cerrar sesión" hacia abajo
        
        btnCerrarSesion = crearBotonMenu("Cerrar Sesión");
        btnCerrarSesion.setForeground(new Color(255, 200, 200)); 
        panelLateral.add(btnCerrarSesion);

        this.add(panelLateral, BorderLayout.WEST);

        // ================= PANEL CONTENIDO BLANCO (CARD LAYOUT) =================
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(colorFondoPrincipal);

        // Aquí agregaremos las diferentes vistas (Paneles)
        panelContenido.add(new PanelDashboard(usuarioLogueado, this), "Dashboard");
        panelContenido.add(new PanelProductos(), "Productos");
        panelContenido.add(new PanelVentas(usuarioLogueado), "Ventas");
        panelContenido.add(new PanelInventario(), "Inventario");
        panelContenido.add(new PanelUsuarios(), "Usuarios");
        panelContenido.add(new PanelReportes(), "Reportes");
        panelContenido.add(new PanelConfiguracion(), "Configuracion");
        
        panelHistorial = new PanelHistorialFacturas(usuarioLogueado, this);
        panelDetalle = new PanelDetalleFactura(this);
        panelResumen = new PanelResumenVentas(usuarioLogueado, this);
        panelVentasDelDia = new PanelVentasDelDia(usuarioLogueado, this);
        
        panelContenido.add(panelHistorial, "Historial");
        panelContenido.add(panelDetalle, "DetalleFactura");
        panelContenido.add(panelResumen, "ResumenVentas");
        panelContenido.add(panelVentasDelDia, "VentasDelDia");

        this.add(panelContenido, BorderLayout.CENTER);

        // ================= EVENTOS DE BOTONES =================
        btnDashboard.addActionListener(e -> cardLayout.show(panelContenido, "Dashboard"));
        btnVentas.addActionListener(e -> cardLayout.show(panelContenido, "Ventas"));
        
        btnResumenVentas.addActionListener(e -> {
            panelResumen.refrescarDatos();
            cardLayout.show(panelContenido, "ResumenVentas");
        });
        
        btnHistorial.addActionListener(e -> {
            panelHistorial.refrescarDatos();
            cardLayout.show(panelContenido, "Historial");
        });
        btnProductos.addActionListener(e -> cardLayout.show(panelContenido, "Productos"));
        
        btnUsuarios.addActionListener(e -> cardLayout.show(panelContenido, "Usuarios"));
        btnReportes.addActionListener(e -> cardLayout.show(panelContenido, "Reportes"));
        btnInventario.addActionListener(e -> cardLayout.show(panelContenido, "Inventario"));
        btnConfiguracion.addActionListener(e -> cardLayout.show(panelContenido, "Configuracion"));
        
        btnCerrarSesion.addActionListener(e -> {
            new Login().setVisible(true);
            this.dispose();
        });
    }

    private JLabel crearEtiquetaCategoria(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        lbl.setForeground(new Color(220, 230, 255));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        btn.setForeground(colorTexto);
        btn.setBackground(colorFondoLateral);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(80, 130, 230)); // Azul más oscuro al pasar mouse
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorFondoLateral);
            }
        });
        return btn;
    }

    private void configurarRoles() {
        String rol = usuarioLogueado.getRol();
        
        if (rol != null && rol.equalsIgnoreCase("Cajero")) {
            // Ocultar botones y etiquetas innecesarias para Cajero
            lblCatPrincipal.setVisible(false);
            btnDashboard.setVisible(false);
            
            btnInventario.setVisible(false);
            btnProductos.setVisible(false);
            btnUsuarios.setVisible(false);
            
            lblCatAdmin.setVisible(false);
            btnReportes.setVisible(false);
            btnConfiguracion.setVisible(false);
            
            // Redirigir por defecto al Punto de Venta
            cardLayout.show(panelContenido, "Ventas");
        } else {
            // Administrador
            btnVentas.setVisible(false); // Ocultar Punto de Venta al Admin
            cardLayout.show(panelContenido, "Dashboard");
        }
    }

    // Método para permitir a los paneles cambiar de vista
    public void navegarA(String nombrePanel) {
        if ("VentasDelDia".equals(nombrePanel)) {
            panelVentasDelDia.refrescarDatos();
        }
        cardLayout.show(panelContenido, nombrePanel);
    }
    
    public void mostrarHistorial() {
        panelHistorial.refrescarDatos();
        cardLayout.show(panelContenido, "Historial");
    }
    
    // Método para navegar al detalle con datos
    public void mostrarDetalleFactura(int idFactura, String numFactura) {
        panelDetalle.setFactura(idFactura, numFactura);
        cardLayout.show(panelContenido, "DetalleFactura");
    }
}
