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
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        // Quitar la barra superior de Windows (minimizar, maximizar, cerrar)
        this.setUndecorated(true);
        // Ajustar a pantalla completa en cualquier dispositivo
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        inicializarComponentes();
        configurarRoles();
    }

    // Declaración de paneles para Lazy Initialization
    private PanelDashboard panelDashboard;
    private PanelProductos panelProductos;
    private PanelVentas panelVentas;
    private PanelInventario panelInventario;
    private PanelUsuarios panelUsuarios;
    private PanelReportes panelReportes;
    private PanelConfiguracion panelConfiguracion;
    
    // Método centralizado para Inicialización Perezosa (Lazy Initialization)
    public void mostrarPanel(String nombrePanel) {
        switch (nombrePanel) {
            case "Dashboard":
                if (panelDashboard == null) {
                    panelDashboard = new PanelDashboard(usuarioLogueado, this);
                    panelContenido.add(panelDashboard, "Dashboard");
                }
                panelDashboard.refrescarDatos();
                break;
            case "Productos":
                if (panelProductos == null) {
                    panelProductos = new PanelProductos();
                    panelContenido.add(panelProductos, "Productos");
                }
                panelProductos.cargarDatosTabla();
                break;
            case "Ventas":
                if (panelVentas == null) {
                    panelVentas = new PanelVentas(usuarioLogueado);
                    panelContenido.add(panelVentas, "Ventas");
                }
                break;
            case "Inventario":
                if (panelInventario == null) {
                    panelInventario = new PanelInventario();
                    panelContenido.add(panelInventario, "Inventario");
                }
                panelInventario.cargarDatosTabla();
                break;
            case "Usuarios":
                if (panelUsuarios == null) {
                    panelUsuarios = new PanelUsuarios();
                    panelContenido.add(panelUsuarios, "Usuarios");
                }
                break;
            case "Reportes":
                if (panelReportes == null) {
                    panelReportes = new PanelReportes();
                    panelContenido.add(panelReportes, "Reportes");
                }
                break;
            case "Configuracion":
                if (panelConfiguracion == null) {
                    panelConfiguracion = new PanelConfiguracion(usuarioLogueado);
                    panelContenido.add(panelConfiguracion, "Configuracion");
                }
                break;
            case "Historial":
                if (panelHistorial == null) {
                    panelHistorial = new PanelHistorialFacturas(usuarioLogueado, this);
                    panelContenido.add(panelHistorial, "Historial");
                }
                panelHistorial.setIdTurnoFiltro(-1); // Limpiar cualquier filtro previo
                panelHistorial.refrescarDatos();
                break;
            case "DetalleFactura":
                if (panelDetalle == null) {
                    panelDetalle = new PanelDetalleFactura(this);
                    panelContenido.add(panelDetalle, "DetalleFactura");
                }
                break;
            case "ResumenVentas":
                if (panelResumen == null) {
                    panelResumen = new PanelResumenVentas(usuarioLogueado, this);
                    panelContenido.add(panelResumen, "ResumenVentas");
                }
                panelResumen.refrescarDatos();
                break;
            case "VentasDelDia":
                if (panelVentasDelDia == null) {
                    panelVentasDelDia = new PanelVentasDelDia(usuarioLogueado, this);
                    panelContenido.add(panelVentasDelDia, "VentasDelDia");
                }
                panelVentasDelDia.refrescarDatos();
                break;
        }
        cardLayout.show(panelContenido, nombrePanel);
    }

    private void inicializarComponentes() {
        // ================= PANEL LATERAL (SIDEBAR AZUL) =================
        panelLateral = new JPanel();
        panelLateral.setPreferredSize(new Dimension(300, 0));
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
        btnDashboard = crearBotonMenu("Dashboard", "/img/reportes.png");
        panelLateral.add(btnDashboard);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 15)));

        lblCatModulos = crearEtiquetaCategoria("MÓDULOS");
        panelLateral.add(lblCatModulos);
        btnVentas = crearBotonMenu("Punto de Venta", "/img/venta.png");
        btnResumenVentas = crearBotonMenu("Ventas", "/img/ventas.png");
        btnHistorial = crearBotonMenu("Historial Facturas", "/img/historial.png");
        btnInventario = crearBotonMenu("Inventario", "/img/inventario.png");
        btnProductos = crearBotonMenu("Gestión Productos", "/img/producto.png");
        btnUsuarios = crearBotonMenu("Usuarios", "/img/usuario.png");
        
        panelLateral.add(btnVentas);
        panelLateral.add(btnResumenVentas);
        panelLateral.add(btnHistorial);
        panelLateral.add(btnInventario);
        panelLateral.add(btnProductos);
        panelLateral.add(btnUsuarios);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 15)));

        lblCatAdmin = crearEtiquetaCategoria("ADMINISTRACIÓN");
        panelLateral.add(lblCatAdmin);
        btnReportes = crearBotonMenu("Reportes", "/img/reporte.png");
        btnConfiguracion = crearBotonMenu("Configuración", "/img/configuraciones.png");
        panelLateral.add(btnReportes);
        panelLateral.add(btnConfiguracion);
        
        panelLateral.add(Box.createVerticalGlue()); // Empujar "Cerrar sesión" hacia abajo
        
        btnCerrarSesion = crearBotonMenu("Cerrar Sesión", "/img/cerrar-sesion.png");
        btnCerrarSesion.setForeground(new Color(255, 200, 200)); 
        panelLateral.add(btnCerrarSesion);

        this.add(panelLateral, BorderLayout.WEST);

        // ================= PANEL CONTENIDO BLANCO (CARD LAYOUT) =================
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(colorFondoPrincipal);
        this.add(panelContenido, BorderLayout.CENTER);

        // ================= EVENTOS DE BOTONES =================
        btnDashboard.addActionListener(e -> mostrarPanel("Dashboard"));
        btnVentas.addActionListener(e -> mostrarPanel("Ventas"));
        btnResumenVentas.addActionListener(e -> mostrarPanel("ResumenVentas"));
        btnHistorial.addActionListener(e -> mostrarPanel("Historial"));
        btnProductos.addActionListener(e -> mostrarPanel("Productos"));
        btnUsuarios.addActionListener(e -> mostrarPanel("Usuarios"));
        btnReportes.addActionListener(e -> mostrarPanel("Reportes"));
        btnInventario.addActionListener(e -> mostrarPanel("Inventario"));
        btnConfiguracion.addActionListener(e -> mostrarPanel("Configuracion"));
        
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

    private JButton crearBotonMenu(String texto, String rutaIcono) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Yu Gothic UI", Font.BOLD, 18));
        btn.setForeground(colorTexto);
        btn.setBackground(colorFondoLateral);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        
        try {
            java.net.URL url = getClass().getResource(rutaIcono);
            if (url != null) {
                // Escalar el icono a 24x24 px
                ImageIcon iconOriginal = new ImageIcon(url);
                Image imgEscalada = iconOriginal.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(imgEscalada));
                btn.setIconTextGap(15);
            }
        } catch (Exception e) {
            System.out.println("Error cargando ícono: " + rutaIcono);
        }

        // Alinear al centro o a la izquierda según prefieras (izquierda se ve mejor con íconos)
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        // Margen izquierdo para empujar el ícono
        btn.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 5));
        
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(280, 50));
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
            mostrarPanel("Ventas");
        } else {
            // Administrador
            btnVentas.setVisible(false); // Ocultar Punto de Venta al Admin
            mostrarPanel("Dashboard");
        }
    }

    // Método para permitir a los paneles cambiar de vista
    public void navegarA(String nombrePanel) {
        mostrarPanel(nombrePanel);
    }
    
    public void mostrarHistorial() {
        mostrarPanel("Historial");
    }
    
    public void mostrarHistorialTurno(int idTurno) {
        if (panelHistorial == null) {
            panelHistorial = new PanelHistorialFacturas(usuarioLogueado, this);
            panelContenido.add(panelHistorial, "Historial");
        }
        panelHistorial.setIdTurnoFiltro(idTurno);
        panelHistorial.refrescarDatos();
        cardLayout.show(panelContenido, "Historial");
    }
    
    // Método para navegar al detalle con datos
    public void mostrarDetalleFactura(int idFactura, String numFactura) {
        if (panelDetalle == null) {
            panelDetalle = new PanelDetalleFactura(this);
            panelContenido.add(panelDetalle, "DetalleFactura");
        }
        panelDetalle.setFactura(idFactura, numFactura);
        cardLayout.show(panelContenido, "DetalleFactura");
    }
}
