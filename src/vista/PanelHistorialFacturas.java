package vista;

import conexion.conexion;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo.Usuario;

public class PanelHistorialFacturas extends JPanel {

    private Usuario usuarioLogueado;
    private Menu menuPrincipal;

    private JTable tablaFacturas;
    private DefaultTableModel modeloFacturas;
    
    private JRadioButton rbFiltroFecha, rbFiltroCajero;
    private JComboBox<String> cbFechasGlobales, cbCajeros, cbFechasCajero;
    private JPanel panelOpcionesFiltro;
    private java.util.List<Object[]> listaUsuarios;

    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public PanelHistorialFacturas(Usuario usuario, Menu menu) {
        this.usuarioLogueado = usuario;
        this.menuPrincipal = menu;

        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(colorFondo);
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
    }

    // Método que se llama cuando se muestra el panel para refrescar datos
    public void refrescarDatos() {
        cargarHistorial();
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(colorFondo);

        // Botón Volver
        JButton btnVolver = new JButton("← Volver al Resumen de Ventas");
        btnVolver.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnVolver.setForeground(colorAzulPrincipal);
        btnVolver.setBackground(colorFondo);
        btnVolver.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        btnVolver.setFocusPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnVolver.addActionListener(e -> {
            menuPrincipal.navegarA("ResumenVentas");
        });

        String titulo = usuarioLogueado.getRol() != null && (usuarioLogueado.getRol().equalsIgnoreCase("Administrador") || usuarioLogueado.getRol().equalsIgnoreCase("Admin"))
                ? "Historial de Facturas"
                : "Historial de Facturas (Mis Ventas)";
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);

        JLabel lblSubtitulo = new JLabel("Haz doble clic en una factura para ver los detalles");
        lblSubtitulo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(Color.GRAY);

        JPanel panelTextos = new JPanel(new GridLayout(3, 1));
        panelTextos.setBackground(colorFondo);
        panelTextos.add(btnVolver);
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSubtitulo);

        panelNorte.add(panelTextos, BorderLayout.WEST);
        
        // Filtro para el administrador
        boolean isAdmin = usuarioLogueado.getRol() != null && (usuarioLogueado.getRol().equalsIgnoreCase("Administrador") || usuarioLogueado.getRol().equalsIgnoreCase("Admin"));
        if (isAdmin) {
            JPanel panelFiltroGeneral = new JPanel(new BorderLayout());
            panelFiltroGeneral.setBackground(colorFondo);
            
            // Radio buttons para seleccionar el modo
            JPanel panelRadios = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            panelRadios.setBackground(colorFondo);
            
            JLabel lblFiltro = new JLabel("Filtrar por:");
            lblFiltro.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
            
            rbFiltroFecha = new JRadioButton("Fechas", true);
            rbFiltroCajero = new JRadioButton("Cajeros");
            rbFiltroFecha.setBackground(colorFondo);
            rbFiltroCajero.setBackground(colorFondo);
            
            ButtonGroup bg = new ButtonGroup();
            bg.add(rbFiltroFecha);
            bg.add(rbFiltroCajero);
            
            panelRadios.add(lblFiltro);
            panelRadios.add(rbFiltroFecha);
            panelRadios.add(rbFiltroCajero);
            
            // Panel que cambiará de contenido según el radio button
            panelOpcionesFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelOpcionesFiltro.setBackground(colorFondo);
            
            cbFechasGlobales = new JComboBox<>();
            cbCajeros = new JComboBox<>();
            cbFechasCajero = new JComboBox<>();
            
            Dimension comboDim = new Dimension(150, 30);
            cbFechasGlobales.setPreferredSize(comboDim);
            cbCajeros.setPreferredSize(new Dimension(180, 30));
            cbFechasCajero.setPreferredSize(comboDim);
            
            // Cargar usuarios
            controlador.Ctrl_Usuario ctrlUsr = new controlador.Ctrl_Usuario();
            listaUsuarios = ctrlUsr.obtenerTodos();
            for (Object[] u : listaUsuarios) {
                if (u[6].equals("Activo")) {
                    cbCajeros.addItem(u[1] + " " + u[2]); // Nombre Apellido
                }
            }
            
            // Eventos para cambiar la vista
            rbFiltroFecha.addActionListener(e -> actualizarPanelFiltros());
            rbFiltroCajero.addActionListener(e -> actualizarPanelFiltros());
            
            // Eventos para recargar historial
            cbFechasGlobales.addActionListener(e -> cargarHistorial());
            cbCajeros.addActionListener(e -> {
                cargarFechasPorCajero();
                cargarHistorial();
            });
            cbFechasCajero.addActionListener(e -> cargarHistorial());
            
            panelFiltroGeneral.add(panelRadios, BorderLayout.NORTH);
            panelFiltroGeneral.add(panelOpcionesFiltro, BorderLayout.CENTER);
            
            panelNorte.add(panelFiltroGeneral, BorderLayout.EAST);
            
        }

        this.add(panelNorte, BorderLayout.NORTH);

        // Tabla
        String[] columnas = { "ID", "N° FACTURA", "FECHA / HORA", "TOTAL", "MÉTODO PAGO" };
        modeloFacturas = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaFacturas = new JTable(modeloFacturas);
        tablaFacturas.setBackground(Color.WHITE);
        tablaFacturas.setForeground(colorTexto);
        tablaFacturas.setRowHeight(35);
        tablaFacturas.setGridColor(new Color(230, 230, 230));
        tablaFacturas.setSelectionBackground(new Color(200, 220, 255));
        tablaFacturas.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));

        JTableHeader header = tablaFacturas.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Evento de doble clic para abrir detalles en el mismo panel de contenido
        tablaFacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    abrirDetalles();
                }
            }
        });

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(new EmptyBorder(20, 0, 0, 0));
        panelCentro.setBackground(colorFondo);
        panelCentro.add(scrollPane, BorderLayout.CENTER);

        this.add(panelCentro, BorderLayout.CENTER);
        
        // Cargar datos iniciales DESPUÉS de haber instanciado la tabla
        if (isAdmin) {
            cargarFechasGlobales();
            actualizarPanelFiltros();
        }
    }

    private int idTurnoFiltro = -1;

    public void setIdTurnoFiltro(int idTurno) {
        this.idTurnoFiltro = idTurno;
    }

    public void cargarHistorial() {
        modeloFacturas.setRowCount(0);
        Object[] filaCarga = new Object[]{"Cargando...", "", "", "", ""};
        modeloFacturas.addRow(filaCarga);
        
        final int idTurnoActual = this.idTurnoFiltro;
        final boolean isAdminFinal = usuarioLogueado.getRol() != null && (usuarioLogueado.getRol().equalsIgnoreCase("Administrador") || usuarioLogueado.getRol().equalsIgnoreCase("Admin"));
        
        final boolean isFiltroFecha = rbFiltroFecha != null && rbFiltroFecha.isSelected();
        final String fechaGlobalSeleccionada = (cbFechasGlobales != null && cbFechasGlobales.getSelectedItem() != null) ? cbFechasGlobales.getSelectedItem().toString() : null;
        
        final boolean isFiltroCajero = rbFiltroCajero != null && rbFiltroCajero.isSelected();
        final String cajeroSeleccionado = (cbCajeros != null && cbCajeros.getSelectedItem() != null) ? cbCajeros.getSelectedItem().toString() : null;
        final String fechaCajeroSeleccionada = (cbFechasCajero != null && cbFechasCajero.getSelectedItem() != null) ? cbFechasCajero.getSelectedItem().toString() : null;
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                java.util.List<Object[]> resultados = new java.util.ArrayList<>();
                Connection cn = conexion.conectar();
                try {
                    // Solo facturas del turno activo, o del día si no hay turno, O del filtro
                    StringBuilder sql = new StringBuilder(
                            "SELECT f.idFactura, f.numeroFactura, f.fechaFactura, f.totalPagar, p.metodoPago " +
                                    "FROM tb_factura f " +
                                    "LEFT JOIN tb_pago p ON f.idFactura = p.idFactura " +
                                    "WHERE 1=1 ");

                    if (idTurnoActual != -1) {
                        sql.append("AND f.idTurno = ").append(idTurnoActual).append(" ");
                    } else {
                        if (isAdminFinal) {
                            if (isFiltroFecha) {
                                // Modo Fecha global
                                if (fechaGlobalSeleccionada != null) {
                                    sql.append("AND DATE(f.fechaFactura) = '").append(fechaGlobalSeleccionada).append("' ");
                                }
                            } else if (isFiltroCajero) {
                                // Modo Cajero
                                int idU = -1;
                                if (cajeroSeleccionado != null) {
                                    for (Object[] u : listaUsuarios) {
                                        if ((u[1] + " " + u[2]).equals(cajeroSeleccionado)) {
                                            idU = (int) u[0];
                                            break;
                                        }
                                    }
                                }
                                if (idU != -1) {
                                    sql.append("AND f.idUsuario = ").append(idU).append(" ");
                                }
                                
                                // Fecha del cajero (opcional)
                                if (fechaCajeroSeleccionada != null && !fechaCajeroSeleccionada.equals("Todas las fechas")) {
                                    sql.append("AND DATE(f.fechaFactura) = '").append(fechaCajeroSeleccionada).append("' ");
                                }
                            } else {
                                // Respaldo por defecto
                                sql.append("AND DATE(f.fechaFactura) = CURDATE() ");
                            }
                        } else {
                            // El cajero ve SOLAMENTE las ventas de su turno activo actual
                            sql.append("AND f.idTurno IN (SELECT idTurno FROM tb_turno WHERE estado = 1 AND idUsuario = ").append(usuarioLogueado.getIdUsuario()).append(") ");
                        }
                    }

                    if (!isAdminFinal) {
                        sql.append("AND f.idUsuario = ? ");
                    }
                    sql.append("ORDER BY f.idFactura DESC");

                    PreparedStatement ps = cn.prepareStatement(sql.toString());
                    if (!isAdminFinal) {
                        ps.setInt(1, usuarioLogueado.getIdUsuario());
                    }
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        Object[] fila = new Object[5];
                        fila[0] = rs.getInt("idFactura");
                        fila[1] = rs.getString("numeroFactura");
                        fila[2] = rs.getString("fechaFactura");
                        fila[3] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f",
                                rs.getDouble("totalPagar"));
                        fila[4] = rs.getString("metodoPago") != null ? rs.getString("metodoPago") : "N/A";
                        resultados.add(fila);
                    }
                    cn.close();
                } catch (SQLException e) {
                    System.out.println("Error al cargar historial: " + e);
                }
                return resultados;
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloFacturas.setRowCount(0);
                    for (Object[] fila : datos) {
                        modeloFacturas.addRow(fila);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void abrirDetalles() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1)
            return;

        int idFactura = (int) tablaFacturas.getValueAt(fila, 0);
        String numFactura = tablaFacturas.getValueAt(fila, 1).toString();

        // Pasamos la orden al Menu para que configure y muestre el PanelDetalleFactura
        menuPrincipal.mostrarDetalleFactura(idFactura, numFactura);
    }
    
    // --- MÉTODOS DE FILTRO DINÁMICO ---
    private void actualizarPanelFiltros() {
        panelOpcionesFiltro.removeAll();
        if (rbFiltroFecha.isSelected()) {
            panelOpcionesFiltro.add(new JLabel("Elegir Fecha:"));
            panelOpcionesFiltro.add(cbFechasGlobales);
        } else {
            panelOpcionesFiltro.add(new JLabel("Cajero:"));
            panelOpcionesFiltro.add(cbCajeros);
            panelOpcionesFiltro.add(new JLabel("Fecha:"));
            panelOpcionesFiltro.add(cbFechasCajero);
            
            if (cbCajeros.getSelectedItem() != null) {
                cargarFechasPorCajero();
            }
        }
        panelOpcionesFiltro.revalidate();
        panelOpcionesFiltro.repaint();
        cargarHistorial(); // Refrescar historial con el nuevo modo
    }

    private void cargarFechasGlobales() {
        cbFechasGlobales.removeActionListener(cbFechasGlobales.getActionListeners()[0]); // Pausar eventos
        cbFechasGlobales.removeAllItems();
        cbFechasGlobales.addItem("Cargando...");
        
        javax.swing.SwingWorker<java.util.List<String>, Void> worker = new javax.swing.SwingWorker<java.util.List<String>, Void>() {
            @Override
            protected java.util.List<String> doInBackground() throws Exception {
                java.util.List<String> fechas = new java.util.ArrayList<>();
                try {
                    Connection cn = conexion.conectar();
                    PreparedStatement ps = cn.prepareStatement("SELECT DISTINCT DATE(fechaFactura) as fecha FROM tb_factura ORDER BY fecha DESC");
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()) {
                        fechas.add(rs.getString("fecha"));
                    }
                    cn.close();
                } catch(Exception e){}
                return fechas;
            }

            @Override
            protected void done() {
                try {
                    java.util.List<String> fechas = get();
                    cbFechasGlobales.removeAllItems();
                    for (String f : fechas) {
                        cbFechasGlobales.addItem(f);
                    }
                    cbFechasGlobales.addActionListener(e -> cargarHistorial()); // Reactivar
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void cargarFechasPorCajero() {
        if (cbCajeros.getSelectedItem() == null) return;
        
        // Remover listener temporalmente para evitar llamadas dobles
        if (cbFechasCajero.getActionListeners().length > 0) {
            cbFechasCajero.removeActionListener(cbFechasCajero.getActionListeners()[0]);
        }
        
        cbFechasCajero.removeAllItems();
        cbFechasCajero.addItem("Todas las fechas");
        
        int idU = -1;
        String nombreCajero = cbCajeros.getSelectedItem().toString();
        for (Object[] u : listaUsuarios) {
            if ((u[1] + " " + u[2]).equals(nombreCajero)) {
                idU = (int) u[0];
                break;
            }
        }
        
        if (idU != -1) {
            try {
                Connection cn = conexion.conectar();
                PreparedStatement ps = cn.prepareStatement("SELECT DISTINCT DATE(fechaFactura) as fecha FROM tb_factura WHERE idUsuario = ? ORDER BY fecha DESC");
                ps.setInt(1, idU);
                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    cbFechasCajero.addItem(rs.getString("fecha"));
                }
                cn.close();
            } catch(Exception e){}
        }
        
        cbFechasCajero.addActionListener(e -> cargarHistorial());
    }
}
