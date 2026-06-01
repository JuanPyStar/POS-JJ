package vista;

import controlador.Ctrl_Dashboard;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelInventario extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);
    
    private DefaultTableModel modeloTabla;
    private JButton btnMovimiento;
    private JButton btnVerTodos;
    private JButton btnVerEntradas;
    private JButton btnVerSalidas;
    
    private String tipoFiltroActivo = null;
    private JTextField txtBuscarProducto;
    private JComboBox<String> cbFechasInventario;

    public PanelInventario() {
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
        cargarDatosTabla(null);
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Control de Inventario");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        btnMovimiento = new JButton("+ Registrar Movimiento");
        btnMovimiento.setBackground(new Color(46, 204, 113)); // Verde
        btnMovimiento.setForeground(Color.WHITE);
        btnMovimiento.setFocusPainted(false);
        btnMovimiento.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnMovimiento.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        btnMovimiento.addActionListener(e -> {
            FrmMovimientoInventario frm = new FrmMovimientoInventario((Frame) SwingUtilities.getWindowAncestor(this));
            frm.setVisible(true);
            cargarDatosTabla(null);
        });
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnMovimiento, BorderLayout.EAST);

        // --- FILTROS ---
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelFiltros.setBackground(colorFondo);
        panelFiltros.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        btnVerTodos = new JButton("Todos");
        btnVerTodos.setBackground(colorAzulPrincipal);
        btnVerTodos.setForeground(Color.WHITE);
        btnVerTodos.setFocusPainted(false);
        btnVerTodos.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnVerTodos.addActionListener(e -> aplicarFiltro(null));

        btnVerEntradas = new JButton("Entradas");
        btnVerEntradas.setBackground(new Color(46, 204, 113));
        btnVerEntradas.setForeground(Color.WHITE);
        btnVerEntradas.setFocusPainted(false);
        btnVerEntradas.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnVerEntradas.addActionListener(e -> aplicarFiltro("ENTRADA"));

        btnVerSalidas = new JButton("Salidas");
        btnVerSalidas.setBackground(new Color(231, 76, 60));
        btnVerSalidas.setForeground(Color.WHITE);
        btnVerSalidas.setFocusPainted(false);
        btnVerSalidas.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnVerSalidas.addActionListener(e -> aplicarFiltro("SALIDA"));
        
        panelFiltros.add(btnVerTodos);
        panelFiltros.add(btnVerEntradas);
        panelFiltros.add(btnVerSalidas);
        
        // Separador visual
        panelFiltros.add(Box.createRigidArea(new Dimension(20, 0)));
        
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        
        txtBuscarProducto = new JTextField();
        txtBuscarProducto.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        txtBuscarProducto.setPreferredSize(new Dimension(150, 32));
        
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        
        cbFechasInventario = new JComboBox<>();
        cbFechasInventario.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        cbFechasInventario.setPreferredSize(new Dimension(150, 32));
        cargarFechasMovimientos();
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(colorAzulPrincipal);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnBuscar.addActionListener(e -> aplicarFiltro(tipoFiltroActivo));
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(231, 76, 60)); // Rojo
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnLimpiar.addActionListener(e -> {
            txtBuscarProducto.setText("");
            if(cbFechasInventario.getItemCount() > 0) {
                cbFechasInventario.setSelectedIndex(0);
            }
            tipoFiltroActivo = null;
            aplicarFiltro(null);
        });
        
        panelFiltros.add(lblProducto);
        panelFiltros.add(txtBuscarProducto);
        panelFiltros.add(lblFecha);
        panelFiltros.add(cbFechasInventario);
        panelFiltros.add(btnBuscar);
        panelFiltros.add(btnLimpiar);

        // --- PANEL NORTE ---
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(colorFondo);
        
        panelNorte.add(panelHeader);
        panelNorte.add(panelFiltros);
        
        this.add(panelNorte, BorderLayout.NORTH);

        // --- TABLA DE INVENTARIO ---
        String[] columnas = {"ID", "PRODUCTO", "TIPO MOV.", "CANTIDAD", "FECHA"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabla = new JTable(modeloTabla);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(colorTexto);
        tabla.setRowHeight(45);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));

        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 17));
        header.setPreferredSize(new Dimension(0, 55));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        this.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void cargarFechasMovimientos() {
        cbFechasInventario.removeAllItems();
        cbFechasInventario.addItem("Cargando...");
        javax.swing.SwingWorker<java.util.List<String>, Void> worker = new javax.swing.SwingWorker<java.util.List<String>, Void>() {
            @Override
            protected java.util.List<String> doInBackground() throws Exception {
                java.util.List<String> fechas = new java.util.ArrayList<>();
                try {
                    java.sql.Connection cn = conexion.conexion.conectar();
                    java.sql.PreparedStatement ps = cn.prepareStatement("SELECT DISTINCT DATE(fechaMovimiento) as fecha FROM tb_movimiento_inventario ORDER BY fecha DESC");
                    java.sql.ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        fechas.add(rs.getString("fecha"));
                    }
                    cn.close();
                } catch (Exception e) {}
                return fechas;
            }

            @Override
            protected void done() {
                try {
                    java.util.List<String> fechas = get();
                    cbFechasInventario.removeAllItems();
                    cbFechasInventario.addItem("Todas las fechas");
                    for (String f : fechas) {
                        cbFechasInventario.addItem(f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void aplicarFiltro(String tipo) {
        this.tipoFiltroActivo = tipo;
        String busqueda = txtBuscarProducto.getText();
        String fecha = cbFechasInventario.getSelectedItem() != null ? cbFechasInventario.getSelectedItem().toString() : "Todas las fechas";
        if (fecha.equals("Cargando...")) {
            fecha = "Todas las fechas";
        }
        final String fechaQuery = fecha;

        modeloTabla.setRowCount(0);
        Object[] filaCarga = new Object[]{"Cargando...", "", "", "", ""};
        modeloTabla.addRow(filaCarga);
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
                return ctrl.getMovimientosInventario(tipo, busqueda, fechaQuery);
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloTabla.setRowCount(0);
                    for (Object[] fila : datos) {
                        Object[] nuevaFila = { fila[0], fila[1], fila[3], fila[4], fila[5] };
                        modeloTabla.addRow(nuevaFila);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    public void cargarDatosTabla() {
        cargarFechasMovimientos(); // Actualizar fechas por si hubo movimientos nuevos
        aplicarFiltro(null);
    }

    public void cargarDatosTabla(String tipo) {
        aplicarFiltro(tipo);
    }
}
