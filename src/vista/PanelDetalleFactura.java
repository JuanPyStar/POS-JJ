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

public class PanelDetalleFactura extends JPanel {

    private Menu menuPrincipal;
    private int idFactura;
    private String numFactura;
    
    private JLabel lblTitulo;
    private JTable tablaDetalles;
    private DefaultTableModel modeloDetalles;
    
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public PanelDetalleFactura(Menu menu) {
        this.menuPrincipal = menu;
        
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(colorFondo);
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
    }

    public void setFactura(int idFactura, String numFactura) {
        this.idFactura = idFactura;
        this.numFactura = numFactura;
        lblTitulo.setText("Factura: " + numFactura);
        cargarDetalles();
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(colorFondo);
        
        // Botón Volver (Flecha / Casita)
        JButton btnVolver = new JButton("← Volver al Historial");
        btnVolver.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnVolver.setForeground(colorAzulPrincipal);
        btnVolver.setBackground(colorFondo);
        btnVolver.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        btnVolver.setFocusPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnVolver.addActionListener(e -> {
            menuPrincipal.navegarA("Historial");
        });
        
        lblTitulo = new JLabel("Factura: ");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(colorFondo);
        panelTextos.add(btnVolver);
        panelTextos.add(lblTitulo);
        
        panelNorte.add(panelTextos, BorderLayout.WEST);
        this.add(panelNorte, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"PRODUCTO", "CANTIDAD", "P. UNITARIO", "SUBTOTAL"};
        modeloDetalles = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaDetalles = new JTable(modeloDetalles);
        tablaDetalles.setBackground(Color.WHITE);
        tablaDetalles.setForeground(colorTexto);
        tablaDetalles.setRowHeight(35);
        tablaDetalles.setGridColor(new Color(230, 230, 230));
        tablaDetalles.setSelectionBackground(new Color(200, 220, 255));
        tablaDetalles.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));

        JTableHeader header = tablaDetalles.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(new EmptyBorder(20, 0, 0, 0));
        panelCentro.setBackground(colorFondo);
        panelCentro.add(scrollPane, BorderLayout.CENTER);

        this.add(panelCentro, BorderLayout.CENTER);
        
        // Panel Sur (Botón Imprimir)
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setBackground(colorFondo);
        panelSur.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton btnImprimir = new JButton("Imprimir Factura");
        btnImprimir.setBackground(new Color(46, 204, 113)); // Verde
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnImprimir.setPreferredSize(new Dimension(250, 45));
        btnImprimir.setFocusPainted(false);
        
        btnImprimir.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Generando e imprimiendo el Ticket de la factura " + numFactura + "...");
        });
        
        panelSur.add(btnImprimir);
        this.add(panelSur, BorderLayout.SOUTH);
    }

    private void cargarDetalles() {
        modeloDetalles.setRowCount(0);
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT p.nombre, d.cantidad, d.precioUnitario, d.subtotal " +
                         "FROM tb_detalle_factura d " +
                         "INNER JOIN tb_producto p ON d.idProducto = p.idProducto " +
                         "WHERE d.idFactura = ?";
                         
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idFactura);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getString("nombre");
                fila[1] = rs.getInt("cantidad");
                fila[2] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", rs.getDouble("precioUnitario"));
                fila[3] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", rs.getDouble("subtotal"));
                modeloDetalles.addRow(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al cargar detalles de factura: " + e);
        }
    }
}
